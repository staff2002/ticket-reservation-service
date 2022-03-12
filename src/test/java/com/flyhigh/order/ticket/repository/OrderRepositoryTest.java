package com.flyhigh.order.ticket.repository;

import com.flyhigh.order.ticket.bases.TestBase;
import com.flyhigh.order.ticket.enums.FlightStatus;
import com.flyhigh.order.ticket.enums.OrderStatus;
import com.flyhigh.order.ticket.repository.entity.OrderEntity;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import javax.transaction.Transactional;
import java.util.Date;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class OrderRepositoryTest extends TestBase {
    @Autowired
    OrderRepository orderRepository;

    @Test
    public void should_save_order_correctly_given_valid_order(){
        OrderEntity order = OrderEntity.builder()
                .userId(10L)
                .flightId(1L)
                .flightStatus(FlightStatus.CHANGED)
                .createdAt(new Date())
                .build();

        OrderEntity savedOrder = orderRepository.save(order);


        assertNotNull(savedOrder);
        assertEquals(1L, savedOrder.getFlightId());
        assertEquals(FlightStatus.CHANGED, savedOrder.getFlightStatus());
        assertEquals(10L, savedOrder.getUserId());
    }

    @Test
    @Transactional
    public void should_update_order_status_correctly_given_existed_order(){
        OrderEntity order = OrderEntity.builder()
                .userId(10L)
                .flightId(1L)
                .status(OrderStatus.PAYMENT_CONFIRMED)
                .flightStatus(FlightStatus.CHANGED)
                .createdAt(new Date())
                .build();

        orderRepository.save(order);

        order.setStatus(OrderStatus.INVOICING);
        orderRepository.save(order);

        OrderEntity OrderUpdated = orderRepository.getById(order.getId());

        assertNotNull(OrderUpdated);
        assertEquals(OrderStatus.INVOICING, OrderUpdated.getStatus());
    }

    @Test
    public void should_return_empty_given_no_matched_order(){
        Optional<OrderEntity> orderOptional= orderRepository.findById(1L);
        assertTrue(!orderOptional.isPresent());
    }
}
