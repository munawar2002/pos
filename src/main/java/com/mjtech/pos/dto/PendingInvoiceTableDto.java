package com.mjtech.pos.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PendingInvoiceTableDto {
    private String orderNo;
    private String status;
    private String orderDate;
    private String customer;
    private Double amount;
    private int invoiceId;
}
