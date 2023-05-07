package com.mjtech.pos.repository;

import com.mjtech.pos.entity.InvoiceDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InvoiceDetailRepository extends JpaRepository<InvoiceDetail, Integer> {

    List<InvoiceDetail> findByInvoiceId(int invoiceId);
}
