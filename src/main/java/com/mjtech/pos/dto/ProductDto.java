package com.mjtech.pos.dto;

import lombok.Data;

import java.util.Date;

@Data
public class ProductDto {
    private int id;
    private String code;
    private String name;
    private int productCompanyId;
    private String productCompanyName;
    private int categoryId;
    private String categoryName;
    private int supplierId;
    private String supplierName;
    private Double buyPrice;
    private int quantity;
    private Double sellPrice;
    private String createdBy;
    private Date createdAt;
    private String updatedBy;
    private Date updatedAt;
    private byte[] image;

}
