package com.mjtech.pos.entity;

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
@Table(name = "PRODUCT")
@Builder
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "code")
    private String code;

    @Column(name = "name")
    private String name;

    @Column(name = "quantity")
    private int quantity;

    @Column(name = "product_company_id")
    private int productCompanyId;

    @Column(name = "category_id")
    private int categoryId;

    @Column(name = "supplier_id")
    private int supplierId;

    @Column(name = "buy_price")
    private Double buyPrice;

    @Column(name = "active")
    private boolean active;

    @Column(name = "sell_price")
    private Double sellPrice;

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
