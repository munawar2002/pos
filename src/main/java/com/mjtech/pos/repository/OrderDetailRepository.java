package com.mjtech.pos.repository;

import com.mjtech.pos.entity.OrderDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderDetailRepository extends JpaRepository<OrderDetail, Integer> {

    List<OrderDetail> findByOrderId(int orderId);

    void deleteByOrderId(int orderId);

}
