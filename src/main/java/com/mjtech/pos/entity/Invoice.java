package com.mjtech.pos.entity;


import com.mjtech.pos.constant.InvoiceStatus;
import com.mjtech.pos.constant.PaymentType;
import com.mjtech.pos.util.ActiveUser;
import com.mjtech.pos.util.ActiveTerminal;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "INVOICE")
@Builder
public class Invoice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "order_id", nullable = true)
    private Order order;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "refund_order_id", nullable = true)
    private RefundOrder refundOrder;

    @OneToMany(mappedBy = "invoice", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<InvoiceDetail> invoiceDetails = new ArrayList<>();

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private InvoiceStatus status;

    @Column(name = "invoice_date")
    private Date invoiceDate;

    @Column(name = "amount")
    private Double amount;

    @Column(name = "is_refunded")
    private boolean isRefunded;

    @Column(name = "remarks")
    private String remarks;

    @Column(name = "invoice_no")
    private String invoiceNo;

    @Column(name = "refund_amount")
    private Double refundAmount;

    @Column(name = "STATUS_CHANGE_DATE")
    private Date statusChangeDate;

    @Column(name = "tax_percentage")
    private Double taxPercentage;

    @Column(name = "gst")
    private Double gst;

    @Column(name = "DISCOUNT_AMOUNT")
    private Double discountAmount;

    @Column(name = "DISCOUNT_Percentage")
    private Double discountPercentage;

    @Column(name = "TOTAL_DISCOUNT")
    private Double totalDiscount;

    @Column(name = "TOTAL_AMOUNT")
    private Double totalAmount;

    @Column(name = "CASH_RECEIVED")
    private Double cashReceived;

    @Column(name = "CARD_RECEIVED")
    private Double cardReceived;

    @Column(name = "BALANCE_AMOUNT")
    private Double balanceAmount;

    @Column(name = "TOTAL_RECEIVED")
    private Double totalReceived;

    @Column(name = "payment_type")
    @Enumerated(EnumType.STRING)
    private PaymentType paymentType;

    @Column(name = "active")
    private boolean active;

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
