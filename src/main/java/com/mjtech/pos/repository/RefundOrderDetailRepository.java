package com.mjtech.pos.repository;

import com.mjtech.pos.entity.RefundOrder;
import com.mjtech.pos.entity.RefundOrderDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RefundOrderDetailRepository extends JpaRepository<RefundOrderDetail, Integer> {

    List<RefundOrderDetail> findByRefundOrderId(int refundOrderId);

    void deleteByRefundOrder(RefundOrder refundOrder);
}
