package com.mjtech.pos.service;

import com.mjtech.pos.constant.InvoiceStatus;
import com.mjtech.pos.constant.OrderStatus;
import com.mjtech.pos.dto.OrderTableDto;
import com.mjtech.pos.entity.*;
import com.mjtech.pos.entity.Order;
import com.mjtech.pos.repository.*;
import com.mjtech.pos.util.DateUtil;
import com.mjtech.pos.util.Util;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderDetailRepository orderDetailRepository;

    @Autowired
    private InvoiceRepository invoiceRepository;

    @Autowired
    private InvoiceDetailRepository invoiceDetailRepository;

    @Autowired
    private ProductRepository productRepository;

    @PersistenceContext
    private EntityManager entityManager;

    @Transactional(propagation = Propagation.REQUIRED)
    public Invoice saveOrder(Customer customer, List<OrderTableDto> orderTableDtos, String orderRemarks) {

        Double total = orderTableDtos.stream().mapToDouble(t -> Double.parseDouble(t.getTotal())).sum();
        log.info("Start creating new order for customer {}, total items {}, total amount {} on date {}",
                customer.getFullName(), orderTableDtos.size(), total, new Date());
        Order order = null;
        boolean existingOrder = orderTableDtos.get(0).isExistingOrder();

        if(existingOrder) {
            int orderId = orderTableDtos.get(0).getOrderId();
            log.info("Found existing order. Started editing the order with id {}", orderId);
            order = orderRepository.findById(orderId)
                    .orElseThrow(() -> new RuntimeException(String.format("Order not found with id %s", orderId)));
        } else {
            order = new Order();
        }
        order.setCustomer(customer);
        order.setStatus(OrderStatus.CREATED);
        order.setOrderDate(new Date());
        order.setAmount(total);
        order.setInvoiced(false);
        order.setRefunded(false);
        order.setStatusChangeDate(new Date());
        order.setRemarks(orderRemarks);

        order = orderRepository.save(order);
        order.setOrderNo(Util.createOrderNo(order.getId()));
        order = orderRepository.save(order);

        if(existingOrder) {
            log.info("Deleting existing orderDetails for orderId {}", order.getId());
            orderDetailRepository.deleteByOrderId(order.getId());
        }


        for(OrderTableDto dto : orderTableDtos) {
            Product product = productRepository.findById(dto.getProductId())
                    .orElseThrow(() -> new RuntimeException("Product not found with id " + dto.getProductId()));
            OrderDetail orderDetail = OrderDetail.builder()
                    .order(order)
                    .product(product)
                    .quantity(dto.getQuantity())
                    .amount(Double.parseDouble(dto.getPrice()))
                    .build();

            orderDetailRepository.save(orderDetail);
        }
        log.info("Created {} orderDetails for orderId {}", orderTableDtos.size(), order.getId());

        Invoice invoice = null;
        if(existingOrder) {
            Order finalOrder = order;
            invoice = invoiceRepository.findByOrderId(finalOrder.getId())
                    .orElseThrow(() -> new RuntimeException(String.format("Invoice not found with orderId %s", finalOrder.getId())));
            log.info("Editing existing invoice with id {} for orderId {}", invoice.getId(), order.getId());
        } else {
            invoice = new Invoice();
            invoice.setInvoiceNo(UUID.randomUUID().toString());
        }
        invoice.setOrder(order);
        invoice.setInvoiceDate(new Date());
        invoice.setAmount(total);
        invoice.setCustomer(customer);
        invoice.setRefunded(false);
        invoice.setStatus(InvoiceStatus.CREATED);
        invoice.setStatusChangeDate(new Date());
        invoice.setRemarks(orderRemarks);

        Invoice savedInvoice = invoiceRepository.save(invoice);

        List<OrderDetail> orderDetails = orderDetailRepository.findByOrderId(order.getId());

        if(existingOrder) {
            invoice.getInvoiceDetails().clear();
            invoiceDetailRepository.deleteByInvoice(invoice);
            log.info("Deleted existing invoice details for invoiceId {} orderId {}", invoice.getId(), order.getId());
        }

        Assert.isTrue(orderDetails.size() == orderTableDtos.size(),
                "User order list and saved order list are mismatched for orderId "+ order.getId());

        for(OrderDetail orderDetail: orderDetails) {
            InvoiceDetail invoiceDetail = InvoiceDetail.builder()
                    .invoice(savedInvoice)
                    .quantity(orderDetail.getQuantity())
                    .product(orderDetail.getProduct())
                    .amount(orderDetail.getAmount())
                    .build();
            invoice.getInvoiceDetails().add(invoiceDetail);
        }
        invoice = invoiceRepository.save(invoice);
        log.info("Created new invoiceDetails for invoiceId {} orderId {}", invoice.getId(), order.getId());
        return invoice;
    }

    public List<Invoice> searchInvoice(Integer customerId, Integer productId, Date fromDate, Date toDate, String orderNo,
                                       String invoiceNo, Double fromAmount, Double toAmount, Double cashReceived,
                                       Double cardReceived, InvoiceStatus invoiceStatus) {

        if(fromAmount == null && toAmount != null) {
            fromAmount = toAmount;
        } else if (toAmount == null && fromAmount != null) {
            toAmount = fromAmount;
        }

        if(fromDate == null && toDate != null) {
            fromDate = toDate;
        } else if (toDate == null && fromDate != null) {
            toDate = fromDate;
        }

        if(toDate != null) {
            toDate = DateUtil.getEndOfDayDate(toDate);
        }

        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Invoice> criteriaQuery = criteriaBuilder.createQuery(Invoice.class);
        Root<Invoice> root = criteriaQuery.from(Invoice.class);
        Join<Invoice, Order> orderJoin = root.join("order", JoinType.INNER);

        List<Predicate> predicates = new ArrayList<>();

        // customerId filter
        if (customerId != null) {
            Join<Invoice, Customer> customerJoin = root.join("customer", JoinType.INNER);
            predicates.add(criteriaBuilder.equal(customerJoin.get("id"), customerId));
        }

        // productId filter
        if (productId != null) {
            Join<Invoice, InvoiceDetail> invoiceItemJoin = root.join("invoiceDetails", JoinType.INNER);
            predicates.add(criteriaBuilder.equal(invoiceItemJoin.get("product").get("id"), productId));
        }

        if (fromDate != null && toDate != null) {
            predicates.add(criteriaBuilder.between(root.get("invoiceDate"), fromDate, toDate));
        }

        // fromDate filter
        if (fromDate != null) {
            predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("invoiceDate"), fromDate));
        }

        // toDate filter
        if (toDate != null) {
            predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("invoiceDate"), toDate));
        }

        // orderNo filter
        if (orderNo != null && !orderNo.isEmpty()) {
            predicates.add(criteriaBuilder.like(orderJoin.get("orderNo"), "%" + orderNo.toLowerCase() + "%"));
        }

        // invoiceNo filter
        if (invoiceNo != null && !invoiceNo.isEmpty()) {
            predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("invoiceNo")),
                    "%" + invoiceNo.toLowerCase() + "%"));
        }

        if (fromAmount != null && toAmount != null) {
            predicates.add(criteriaBuilder.between(root.get("totalAmount"), fromAmount, toAmount));
        }

        // cashReceived filter
        if (cashReceived != null) {
            predicates.add(criteriaBuilder.equal(root.get("cashReceived"), cashReceived));
        }

        // cardReceived filter
        if (cardReceived != null) {
            predicates.add(criteriaBuilder.equal(root.get("cardReceived"), cardReceived));
        }

        // invoiceStatus filter
        if (invoiceStatus != null) {
            predicates.add(criteriaBuilder.equal(root.get("status"), invoiceStatus));
        }

        criteriaQuery.select(root).distinct(true).where(predicates.toArray(new Predicate[]{}));

        TypedQuery<Invoice> query = entityManager.createQuery(criteriaQuery);
        return query.getResultList();
    }


}
