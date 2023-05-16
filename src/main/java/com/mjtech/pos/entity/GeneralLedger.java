package com.mjtech.pos.entity;

import com.mjtech.pos.constant.LedgerType;
import com.mjtech.pos.constant.TransactionType;
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
@Table(name = "GENERAL_LEDGER")
@Builder
public class GeneralLedger {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "account_id", nullable = false)
    private Account account;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "invoice_id", nullable = false)
    private Invoice invoice;

    @Column(name = "transaction_date")
    private Date transactionDate;

    @Column(name = "transaction_type")
    @Enumerated(EnumType.STRING)
    private TransactionType transactionType;

    @Column(name = "ledger_type")
    @Enumerated(EnumType.STRING)
    private LedgerType ledgerType;

    @Column(name = "amount")
    private Double amount;

    @Column(name = "description")
    private String description;

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
