package com.mjtech.pos.entity;


import com.mjtech.pos.constant.OrderStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "ORDER")
@Builder
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "customer_id")
    private int customerId;

    @Column(name = "status")
    private OrderStatus status;

    @Column(name = "order_date")
    private Date orderDate;

    @Column(name = "is_invoiced")
    private boolean isInvoiced;

    @Column(name = "amount")
    private Double amount;

    @Column(name = "cancelled_date")
    private String cancelingDate;

    @Column(name = "cancelled_by")
    private String cancelledBy;

    @Column(name = "cancelled_remarks")
    private String cancelingRemarks;

    @Column(name = "created_by")
    private String createdBy;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_at", columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private Date createdAt;

    @Column(name = "updated_by")
    private String updatedBy;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "updated_at")
    private Date updatedAt;
}
