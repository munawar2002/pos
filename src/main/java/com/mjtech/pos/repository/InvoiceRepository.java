package com.mjtech.pos.repository;

import com.mjtech.pos.constant.InvoiceStatus;
import com.mjtech.pos.entity.Invoice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InvoiceRepository extends JpaRepository<Invoice, Integer>, JpaSpecificationExecutor<Invoice> {

    List<Invoice> findByStatus(InvoiceStatus invoiceStatus);

    Optional<Invoice> findByOrderId(int orderId);

}
