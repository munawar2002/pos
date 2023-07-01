package com.mjtech.pos.service;

import com.mjtech.pos.constant.InvoiceStatus;
import com.mjtech.pos.constant.OrderStatus;
import com.mjtech.pos.dto.OrderTableDto;
import com.mjtech.pos.entity.*;
import com.mjtech.pos.repository.*;
import com.mjtech.pos.util.Util;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.util.*;

@Service
@Slf4j
public class RefundService {

    @Autowired
    private InvoiceRepository invoiceRepository;
    @Autowired
    private InvoiceDetailRepository invoiceDetailRepository;
    @Autowired
    private RefundOrderRepository refundOrderRepository;
    @Autowired
    private RefundOrderDetailRepository refundOrderDetailRepository;
    @Autowired
    private ProductRepository productRepository;

    @Transactional(propagation = Propagation.REQUIRED)
    public Invoice saveRefundOrder(Customer customer, List<OrderTableDto> orderTableDtos, Invoice selectedInvoice) {
        Optional<RefundOrder> refundOrderOptional = refundOrderRepository.findByOrderAndStatus(selectedInvoice.getOrder(), OrderStatus.CREATED);

        Double total = orderTableDtos.stream()
                .mapToDouble(t -> Double.parseDouble(t.getTotal()))
                .sum();
        log.info("Start creating new refund refundOrder for customer {}, total items {}, total amount {} on date {}",
                customer.getFullName(), orderTableDtos.size(), total, new Date());
        RefundOrder refundOrder;
        refundOrder = refundOrderOptional.orElseGet(RefundOrder::new);

        refundOrder.setCustomer(customer);
        refundOrder.setStatus(OrderStatus.CREATED);
        refundOrder.setOrderDate(new Date());
        refundOrder.setOrder(selectedInvoice.getOrder());
        refundOrder.setRefundAmount(total);
        refundOrder.setInvoiced(false);

        refundOrder = refundOrderRepository.save(refundOrder);
        refundOrder.setRefundOrderNo(Util.createOrderNo(refundOrder.getId()));
        refundOrder = refundOrderRepository.save(refundOrder);

        refundOrderOptional.ifPresent(order -> refundOrderDetailRepository.deleteByRefundOrder(order));

        for(OrderTableDto dto : orderTableDtos) {
            Product product = productRepository.findById(dto.getProductId())
                    .orElseThrow(() -> new RuntimeException("Product not found with id " + dto.getProductId()));
            RefundOrderDetail refundOrderDetail = RefundOrderDetail.builder()
                    .refundOrder(refundOrder)
                    .product(product)
                    .quantity(dto.getQuantity())
                    .amount(Double.parseDouble(dto.getPrice()))
                    .build();

            refundOrderDetailRepository.save(refundOrderDetail);
        }
        log.info("Created {} refundOrderDetails for orderId {}", orderTableDtos.size(), refundOrder.getId());

        Invoice invoice;
        if(refundOrderOptional.isPresent()) {
            invoice = invoiceRepository.findByRefundOrderId(refundOrderOptional.get().getId())
                    .orElseThrow(() -> new RuntimeException("Invoice not found with refundOrderId "+ refundOrderOptional.get().getId()));
        } else  {
            invoice = new Invoice();
        }

        invoice.setInvoiceNo(UUID.randomUUID().toString());
        invoice.setRefundOrder(refundOrder);
        invoice.setInvoiceDate(new Date());
        invoice.setAmount(total);
        invoice.setCustomer(customer);
        invoice.setRefunded(false);
        invoice.setStatus(InvoiceStatus.CREATED);
        invoice.setStatusChangeDate(new Date());
        invoice.setRemarks("Refund Invoice");

        Invoice savedInvoice = invoiceRepository.save(invoice);

        if(refundOrderOptional.isPresent()) {
            log.info("deleting invoice details by invoice "+ invoice.getId());
            invoice.getInvoiceDetails().clear();
        }

        List<RefundOrderDetail> refundOrderDetails = refundOrderDetailRepository.findByRefundOrderId(refundOrder.getId());

        for(RefundOrderDetail orderDetail: refundOrderDetails) {
            InvoiceDetail invoiceDetail = InvoiceDetail.builder()
                    .invoice(savedInvoice)
                    .quantity(orderDetail.getQuantity())
                    .product(orderDetail.getProduct())
                    .amount(orderDetail.getAmount())
                    .build();
            invoice.getInvoiceDetails().add(invoiceDetail);
        }
        invoice = invoiceRepository.save(invoice);
        log.info("Created new refundInvoiceDetails for invoiceId {} refundOrderId {}", invoice.getId(), refundOrder.getId());
        return invoice;
    }

}
