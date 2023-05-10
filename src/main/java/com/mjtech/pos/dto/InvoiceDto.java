package com.mjtech.pos.dto;

import com.mjtech.pos.constant.PaymentType;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class InvoiceDto {
    private double cashReceived;
    private double totalAmount;
    private double balanceAmount;
    private double discountAmount;
    private double discountPercent;
    private double discountTotal;
    private double amount;
    private double gst;
    private String remarks;
    private int invoiceId;
    private PaymentType paymentType;
}
