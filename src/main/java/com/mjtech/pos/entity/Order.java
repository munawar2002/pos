package com.mjtech.pos.entity;


import com.mjtech.pos.constant.OrderStatus;
import com.mjtech.pos.util.ActiveUser;
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
@Table(name = "ORDERS")
@Builder
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "customer_id")
    private int customerId;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    @Column(name = "order_date")
    private Date orderDate;

    @Column(name = "amount")
    private Double amount;

    @Column(name = "is_invoiced")
    private boolean isInvoiced;

    @Column(name = "is_refunded")
    private boolean isRefunded;

    @Column(name = "remarks")
    private String remarks;

    @Column(name = "order_no")
    private String orderNo;

    @Column(name = "refund_amount")
    private Double refundAmount;

    @Column(name = "STATUS_CHANGE_DATE")
    private Date statusChangeDate;

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

    @PrePersist
    public void prePersist() {
        if(id==0) {
            createdAt = new Date();
            createdBy = ActiveUser.getActiveUsername();
        } else {
            updatedAt = new Date();
            updatedBy = ActiveUser.getActiveUsername();
        }
    }
}
