package com.mjtech.pos.entity;


import com.mjtech.pos.constant.InvoiceStatus;
import com.mjtech.pos.constant.PaymentType;
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
@Table(name = "INVOICE")
@Builder
public class Invoice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "customer_id")
    private int customerId;

    @Column(name = "order_id")
    private int orderId;

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
