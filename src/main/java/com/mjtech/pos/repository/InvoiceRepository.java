package com.mjtech.pos.repository;

import com.mjtech.pos.constant.InvoiceStatus;
import com.mjtech.pos.entity.Invoice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface InvoiceRepository extends JpaRepository<Invoice, Integer>, JpaSpecificationExecutor<Invoice> {

    List<Invoice> findByStatus(InvoiceStatus invoiceStatus);
    Optional<Invoice> findByOrderId(int orderId);

    Optional<Invoice> findByRefundOrderId(int orderId);

    @Query("SELECT SUM(i.totalAmount) FROM Invoice i WHERE DATE(i.createdAt) = CURRENT_DATE")
    Double getTotalAmountToday();

    @Query("SELECT SUM(i.totalAmount) FROM Invoice i WHERE DATE(i.createdAt) = :yesterday")
    Double getTotalAmountLastDay(@Param("yesterday") LocalDate yesterday);

    @Query("SELECT SUM(i.totalAmount) FROM Invoice i WHERE YEAR(i.createdAt) = :year AND MONTH(i.createdAt) = :month")
    Double getTotalAmountByMonth(@Param("year") int year, @Param("month") int month);

    @Query("SELECT SUM(i.cashReceived + i.balanceAmount) FROM Invoice i WHERE DATE(i.createdAt) = CURRENT_DATE")
    Double getCashReceivedToday();

    @Query("SELECT SUM(i.cardReceived) FROM Invoice i WHERE DATE(i.createdAt) = CURRENT_DATE")
    Double getCardReceivedToday();
}
