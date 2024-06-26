package com.mjtech.pos.entity;


import com.mjtech.pos.constant.OrderStatus;
import com.mjtech.pos.util.ActiveUser;
import com.mjtech.pos.util.ActiveTerminal;
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

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

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

    @Column(name = "terminal_id")
    private Integer terminalId;

    @PrePersist
    public void prePersist() {
        createdAt = new Date();
        createdBy = ActiveUser.getActiveUsername();
        terminalId = ActiveTerminal.getTerminalId();
    }

    @PreUpdate
    public void preUpdate() {
        updatedAt = new Date();
        updatedBy = ActiveUser.getActiveUsername();
        terminalId = ActiveTerminal.getTerminalId();
    }
}
