package com.mjtech.pos.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class OrderTableDto {
    private String code;
    private String productName;
    private int productId;
    private int quantity;
    private Double price;
    private Double total;
}
