package com.mjtech.pos.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class InvoiceTableDto {
    private String orderNo;
    private String status;
    private String orderDate;
    private String customer;
    private Double amount;
    private int invoiceId;
    private String remarks;
    private String paymentType;
    private Double totalDiscount;
    private Double cashReceived;
    private Double cardReceived;

}
