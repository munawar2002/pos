package com.mjtech.pos.service;

import com.mjtech.pos.constant.InvoiceStatus;
import com.mjtech.pos.constant.OrderStatus;
import com.mjtech.pos.dto.OrderTableDto;
import com.mjtech.pos.entity.*;
import com.mjtech.pos.repository.InvoiceDetailRepository;
import com.mjtech.pos.repository.InvoiceRepository;
import com.mjtech.pos.repository.OrderDetailRepository;
import com.mjtech.pos.repository.OrderRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.time.LocalDate;
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
        order.setCustomerId(customer.getId());
        order.setStatus(OrderStatus.CREATED);
        order.setOrderDate(new Date());
        order.setAmount(total);
        order.setInvoiced(false);
        order.setRefunded(false);
        order.setStatusChangeDate(new Date());
        order.setRemarks(orderRemarks);

        order = orderRepository.save(order);
        order.setOrderNo(createOrderNo(order.getId()));
        order = orderRepository.save(order);

        if(existingOrder) {
            log.info("Deleting existing orderDetails for orderId {}", order.getId());
            orderDetailRepository.deleteByOrderId(order.getId());
        }

        for(OrderTableDto dto : orderTableDtos) {
            OrderDetail orderDetail = OrderDetail.builder()
                    .orderId(order.getId())
                    .productId(dto.getProductId())
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
        invoice.setOrderId(order.getId());
        invoice.setInvoiceDate(new Date());
        invoice.setAmount(total);
        invoice.setCustomerId(customer.getId());
        invoice.setRefunded(false);
        invoice.setStatus(InvoiceStatus.CREATED);
        invoice.setStatusChangeDate(new Date());
        invoice.setRemarks(orderRemarks);

        Invoice savedInvoice = invoiceRepository.save(invoice);

        List<OrderDetail> orderDetails = orderDetailRepository.findByOrderId(order.getId());

        if(existingOrder) {
            invoiceDetailRepository.deleteByInvoiceId(invoice.getId());
            log.info("Deleted existing invoice details for invoiceId {} orderId {}", invoice.getId(), order.getId());
        }

        Assert.isTrue(orderDetails.size() == orderTableDtos.size(),
                "User order list and saved order list are mismatched for orderId "+ order.getId());

        for(OrderDetail orderDetail: orderDetails) {
            InvoiceDetail invoiceDetail = InvoiceDetail.builder()
                    .invoiceId(savedInvoice.getId())
                    .quantity(orderDetail.getQuantity())
                    .productId(orderDetail.getProductId())
                    .amount(orderDetail.getAmount())
                    .build();
            invoiceDetailRepository.save(invoiceDetail);
        }
        log.info("Created new invoiceDetails for invoiceId {} orderId {}", invoice.getId(), order.getId());
        return invoice;
    }

    private static String createOrderNo(int orderId) {
        LocalDate today = LocalDate.now();
        return today.getYear() + String.format("%02d", today.getMonth().getValue()) +
                String.format("%02d", today.getDayOfMonth())  + String.format("%06d", orderId);
    }

}
