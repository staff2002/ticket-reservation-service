package com.flyhigh.reservation.order.repository;

import com.flyhigh.reservation.order.repository.entity.OrderEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<OrderEntity,Long> {
}
