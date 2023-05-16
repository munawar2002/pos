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
@Table(name = "ORDER_DETAIL")
@Builder
public class OrderDetail {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "order_id")
    private Order order;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "product_id")
    private Product product;

    @Column(name = "quantity")
    private int quantity;

    @Column(name = "amount")
    private Double amount;

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
