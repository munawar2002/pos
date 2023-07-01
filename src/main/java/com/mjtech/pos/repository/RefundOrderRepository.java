package com.mjtech.pos.repository;

import com.mjtech.pos.constant.OrderStatus;
import com.mjtech.pos.entity.Order;
import com.mjtech.pos.entity.RefundOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RefundOrderRepository extends JpaRepository<RefundOrder, Integer> {
    Optional<RefundOrder> findByRefundOrderNo(String orderNo);

    Optional<RefundOrder> findByOrderAndStatus(Order order, OrderStatus orderStatus);


}
