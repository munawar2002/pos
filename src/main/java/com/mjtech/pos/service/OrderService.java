package com.mjtech.pos.service;

import com.mjtech.pos.constant.InvoiceStatus;
import com.mjtech.pos.constant.OrderStatus;
import com.mjtech.pos.dto.OrderTableDto;
import com.mjtech.pos.entity.*;
import com.mjtech.pos.repository.InvoiceDetailRepository;
import com.mjtech.pos.repository.InvoiceRepository;
import com.mjtech.pos.repository.OrderDetailRepository;
import com.mjtech.pos.repository.OrderRepository;
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
    public Invoice saveOrder(Customer customer, List<OrderTableDto> orderTableDtos) {
        Double total = orderTableDtos.stream().mapToDouble(t -> Double.parseDouble(t.getTotal())).sum();
        Order newOrder = Order.builder()
                .customerId(customer.getId())
                .status(OrderStatus.CREATED)
                .orderDate(new Date())
                .amount(total)
                .isInvoiced(false)
                .isRefunded(false)
                .statusChangeDate(new Date())
                .build();

        Order order = orderRepository.save(newOrder);
        order.setOrderNo(createOrderNo(order.getId()));
        order = orderRepository.save(order);

        for(OrderTableDto dto : orderTableDtos) {
            OrderDetail orderDetail = OrderDetail.builder()
                    .orderId(order.getId())
                    .productId(dto.getProductId())
                    .quantity(dto.getQuantity())
                    .amount(Double.parseDouble(dto.getPrice()))
                    .build();

            orderDetailRepository.save(orderDetail);
        }

        Invoice invoice = Invoice.builder()
                .orderId(order.getId())
                .invoiceDate(new Date())
                .amount(total)
                .invoiceNo(UUID.randomUUID().toString())
                .customerId(customer.getId())
                .isRefunded(false)
                .status(InvoiceStatus.CREATED)
                .statusChangeDate(new Date())
                .build();

        Invoice savedInvoice = invoiceRepository.save(invoice);

        List<OrderDetail> orderDetails = orderDetailRepository.findByOrderId(order.getId());

        Assert.isTrue(orderDetails.size() == orderTableDtos.size(),
                "User order list and saved order list are mismatched!");

        for(OrderDetail orderDetail: orderDetails) {
            InvoiceDetail invoiceDetail = InvoiceDetail.builder()
                    .invoiceId(savedInvoice.getId())
                    .quantity(orderDetail.getQuantity())
                    .productId(orderDetail.getProductId())
                    .amount(orderDetail.getAmount())
                    .build();
            invoiceDetailRepository.save(invoiceDetail);
        }

        return invoice;
    }

    private static String createOrderNo(int orderId) {
        LocalDate today = LocalDate.now();
        return today.getYear() + String.format("%02d", today.getMonth().getValue()) +
                String.format("%02d", today.getDayOfMonth())  + String.format("%06d", orderId);
    }

}
