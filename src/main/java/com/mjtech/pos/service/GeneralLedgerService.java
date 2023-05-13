package com.mjtech.pos.service;

import com.mjtech.pos.constant.AccountName;
import com.mjtech.pos.constant.LedgerType;
import com.mjtech.pos.constant.PaymentType;
import com.mjtech.pos.constant.TransactionType;
import com.mjtech.pos.entity.Account;
import com.mjtech.pos.entity.GeneralLedger;
import com.mjtech.pos.entity.Invoice;
import com.mjtech.pos.repository.AccountRepository;
import com.mjtech.pos.repository.GeneralLedgerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class GeneralLedgerService {

    @Autowired
    private GeneralLedgerRepository generalLedgerRepository;

    @Autowired
    private AccountRepository accountRepository;

    public void createInvoiceSellLedgerEntry(Invoice invoice) {
        Account saleAccount = accountRepository.findByName(AccountName.SALES.name())
                .orElseThrow(() -> new RuntimeException("Account not found with name "+ AccountName.SALES));

        Account cardAccount = accountRepository.findByName(AccountName.CARD.name())
                .orElseThrow(() -> new RuntimeException("Account not found with name "+ AccountName.CARD));

        Account cashAccount = accountRepository.findByName(AccountName.CASH.name())
                .orElseThrow(() -> new RuntimeException("Account not found with name "+ AccountName.CASH));

        createLedgerEntry(LedgerType.CREDIT, TransactionType.SELL,
                saleAccount.getId(), invoice.getTotalAmount(), invoice.getId());

        if(invoice.getPaymentType() == PaymentType.CASH) {
            createLedgerEntry(LedgerType.DEBIT, TransactionType.SELL,
                    cashAccount.getId(), invoice.getTotalAmount(), invoice.getId());
        } else if (invoice.getPaymentType() == PaymentType.CARD) {
            createLedgerEntry(LedgerType.DEBIT, TransactionType.SELL,
                    cardAccount.getId(), invoice.getTotalAmount(), invoice.getId());
        } else if (invoice.getPaymentType() == PaymentType.CASH_AND_CARD) {
            createLedgerEntry(LedgerType.DEBIT, TransactionType.SELL,
                    cashAccount.getId(), invoice.getCashReceived(), invoice.getId());
            createLedgerEntry(LedgerType.DEBIT, TransactionType.SELL,
                    cardAccount.getId(), invoice.getCardReceived(), invoice.getId());
        }
    }


    public void createLedgerEntry(LedgerType ledgerType,
                                  TransactionType transactionType,
                                  int accountId,
                                  Double amount,
                                  int invoiceId) {

        GeneralLedger generalLedger = GeneralLedger.builder()
                .ledgerType(ledgerType)
                .amount(amount)
                .accountId(accountId)
                .transactionDate(new Date())
                .transactionType(transactionType)
                .invoiceId(invoiceId)
                .description("")
                .build();

        generalLedgerRepository.save(generalLedger);
    }

}
