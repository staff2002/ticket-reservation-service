package com.flyhigh.order.ticket.repository;

import com.flyhigh.order.ticket.repository.entity.OrderEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<OrderEntity,Long> {
}
