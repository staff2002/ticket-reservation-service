package com.flyhigh.order.ticket.service;

import com.flyhigh.order.ticket.bases.TestBase;
import com.flyhigh.order.ticket.enums.FlightStatus;
import com.flyhigh.order.ticket.enums.OrderStatus;
import com.flyhigh.order.ticket.enums.FlightChangeCode;
import com.flyhigh.order.ticket.enums.FlightChangeStatus;
import com.flyhigh.order.ticket.feign.dto.FlightChangeResponseFeignDto;
import com.flyhigh.order.ticket.feign.FlightPriceSeatCalculationFeign;
import com.flyhigh.order.ticket.mq.InvoiceMessageSender;
import com.flyhigh.order.ticket.repository.OrderRepository;
import com.flyhigh.order.ticket.repository.entity.OrderEntity;
import com.flyhigh.order.ticket.service.exception.MessageServiceNotAvailableException;
import com.flyhigh.order.ticket.service.exception.NotEnoughSeatException;
import com.flyhigh.order.ticket.service.exception.OrderNotFoundException;
import com.flyhigh.order.ticket.service.model.FlightChangeModel;
import com.flyhigh.order.ticket.service.model.FlightChangeResultModel;
import com.flyhigh.order.ticket.service.model.InvoiceModel;
import com.flyhigh.order.ticket.service.model.InvoiceResultModel;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Optional;
import java.util.concurrent.TimeoutException;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public class OrderServiceTest extends TestBase {
    @MockBean
    private OrderRepository orderRepository;

    @MockBean
    private FlightPriceSeatCalculationFeign flightPriceSeatCalculationFeign;

    @MockBean
    private InvoiceMessageSender invoiceMessageSender;

    @Autowired
    private OrderService orderService;

    @Test
    public void should_change_flight_successfully_given_price_seat_calculation_system_change_successfully() throws TimeoutException {
        final Long originalFlightId = 1L;
        final Long targetFlightId = 2L;

        when(orderRepository.getById(any())).thenReturn(OrderEntity.builder()
                .id(1L)
                .flightId(originalFlightId).flightStatus(FlightStatus.NORMAL).userId(1L).createdAt(new Date()).build());
        when(orderRepository.saveAndFlush(any())).thenReturn(OrderEntity.builder()
                .id(1L)
                .flightId(targetFlightId).flightStatus(FlightStatus.NORMAL).userId(1L).createdAt(new Date()).build());

        when(flightPriceSeatCalculationFeign.changeSeat(any())).thenReturn(FlightChangeResponseFeignDto.builder().status(FlightChangeStatus.SUCCESS).build());

        FlightChangeModel flightChangeModel = FlightChangeModel.builder().orderId(1L).targetFlightId(targetFlightId).build();
        FlightChangeResultModel flightChangeResult = orderService.change(flightChangeModel);

        assertEquals(targetFlightId, flightChangeResult.getChangedFlightId());
    }

    @Test
    public void should_change_flight_failed_given_not_enough_seat() throws TimeoutException {
        final Long originalFlightId = 1L;
        final Long targetFlightId = 2L;

        when(orderRepository.getById(any())).thenReturn(OrderEntity.builder()
                .id(1L)
                .flightId(originalFlightId).flightStatus(FlightStatus.NORMAL).userId(1L).createdAt(new Date()).build());
        when(orderRepository.saveAndFlush(any())).thenReturn(OrderEntity.builder()
                .id(1L)
                .flightId(targetFlightId).flightStatus(FlightStatus.NORMAL).userId(1L).createdAt(new Date()).build());

        when(flightPriceSeatCalculationFeign.changeSeat(any())).thenReturn(FlightChangeResponseFeignDto.builder()
                .status(FlightChangeStatus.FAILED).code(FlightChangeCode.NO_SEAT).build());

        FlightChangeModel flightChangeModel = FlightChangeModel.builder().orderId(1L).targetFlightId(targetFlightId).build();

        assertThrows(NotEnoughSeatException.class,() -> orderService.change(flightChangeModel));
    }

    @Test
    public void should_change_flight_failed_given_price_seat_calculation_system_not_available() throws TimeoutException {
        final Long originalFlightId = 1L;
        final Long targetFlightId = 2L;

        when(orderRepository.getById(any())).thenReturn(OrderEntity.builder()
                .id(1L)
                .flightId(originalFlightId).flightStatus(FlightStatus.NORMAL).userId(1L).createdAt(new Date()).build());
        when(orderRepository.saveAndFlush(any())).thenReturn(OrderEntity.builder()
                .id(1L)
                .flightId(targetFlightId).flightStatus(FlightStatus.NORMAL).userId(1L).createdAt(new Date()).build());

        when(flightPriceSeatCalculationFeign.changeSeat(any())).thenThrow(new TimeoutException());

        FlightChangeModel flightChangeModel = FlightChangeModel.builder().orderId(1L).targetFlightId(targetFlightId).build();

        assertThrows(TimeoutException.class,() -> orderService.change(flightChangeModel));
    }

    @Test
    public void should_invoice_successfully_given_order_existed() {
        when(orderRepository.findById(any())).thenReturn(Optional.of(OrderEntity.builder()
                .id(1L)
                .flightId(1L)
                .flightStatus(FlightStatus.NORMAL)
                .userId(1L)
                .status(OrderStatus.PAYMENT_CONFIRMED)
                .amount(BigDecimal.valueOf(3500L))
                .createdAt(new Date()).build()));

        when(orderRepository.saveAndFlush(any())).thenReturn(OrderEntity.builder()
                .id(1L)
                .flightId(1L).flightStatus(FlightStatus.NORMAL).status(OrderStatus.INVOICING).userId(1L).createdAt(new Date()).build());

        when(invoiceMessageSender.send(any())).thenReturn(true);

        InvoiceModel invoiceModel  = InvoiceModel.builder().orderId(1L).title("李四").email("lisi@163.com").build();

        InvoiceResultModel invoiceResultModel = orderService.invoice(invoiceModel);

        assertEquals(1L,invoiceResultModel.getInvoicedOrderId());
    }

    @Test
    public void should_invoice_failed_given_order_not_existed(){
        when(orderRepository.findById(any())).thenReturn(Optional.empty());

        InvoiceModel invoiceModel  = InvoiceModel.builder().orderId(1L).title("李四").email("lisi@163.com").build();

        assertThrows(OrderNotFoundException.class,() -> orderService.invoice(invoiceModel));
    }

    @Test
    public void should_invoice_failed_given_message_service_not_available(){
        when(orderRepository.findById(any())).thenReturn(Optional.of(OrderEntity.builder()
                .id(1L)
                .flightId(1L)
                .flightStatus(FlightStatus.NORMAL)
                .userId(1L)
                .status(OrderStatus.PAYMENT_CONFIRMED)
                .amount(BigDecimal.valueOf(3500L))
                .createdAt(new Date()).build()));

        when(invoiceMessageSender.send(any())).thenReturn(false);

        InvoiceModel invoiceModel  = InvoiceModel.builder().orderId(1L).title("李四").email("lisi@163.com").build();

        assertThrows(MessageServiceNotAvailableException.class,() -> orderService.invoice(invoiceModel));
    }
}
