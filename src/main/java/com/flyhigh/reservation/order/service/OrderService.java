package com.flyhigh.reservation.order.service;

import com.flyhigh.reservation.order.enums.OrderStatus;
import com.flyhigh.reservation.order.enums.FlightChangeCode;
import com.flyhigh.reservation.order.enums.FlightChangeStatus;
import com.flyhigh.reservation.order.enums.FlightStatus;
import com.flyhigh.reservation.order.feign.dto.FlightChangeRequestFeignDto;
import com.flyhigh.reservation.order.feign.dto.FlightChangeResponseFeignDto;
import com.flyhigh.reservation.order.feign.FlightPriceSeatCalculationFeign;
import com.flyhigh.reservation.order.mq.InvoiceMessageSender;
import com.flyhigh.reservation.order.mq.messages.InvoiceMessage;
import com.flyhigh.reservation.order.repository.OrderRepository;
import com.flyhigh.reservation.order.repository.entity.OrderEntity;
import com.flyhigh.reservation.order.service.exception.MessageServiceNotAvailableException;
import com.flyhigh.reservation.order.service.exception.NotEnoughSeatException;
import com.flyhigh.reservation.order.service.exception.OrderNotFoundException;
import com.flyhigh.reservation.order.service.model.FlightChangeModel;
import com.flyhigh.reservation.order.service.model.FlightChangeResultModel;
import com.flyhigh.reservation.order.service.model.InvoiceModel;
import com.flyhigh.reservation.order.service.model.InvoiceResultModel;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.concurrent.TimeoutException;

@Service
@RequiredArgsConstructor
public class OrderService {
    final OrderRepository orderRepository;
    final FlightPriceSeatCalculationFeign flightPriceSeatCalculationFeign;
    final InvoiceMessageSender invoiceMessageSender;

    @Transactional
    public FlightChangeResultModel change(FlightChangeModel flightChangeModel) throws TimeoutException {
        FlightChangeRequestFeignDto flightChangeRequestFeignDto = getSeatChangeRequestFeignDto(flightChangeModel);

        FlightChangeResponseFeignDto flightChangeResponseFeignDto = flightPriceSeatCalculationFeign.changeSeat(flightChangeRequestFeignDto);

        if (!flightChangeResponseFeignDto.getStatus().equals(FlightChangeStatus.SUCCESS) && flightChangeResponseFeignDto.getCode().equals(FlightChangeCode.NO_SEAT)) {
            throw new NotEnoughSeatException();
        }

        OrderEntity order = orderRepository.getById(flightChangeModel.getOrderId());
        order.setFlightId(flightChangeModel.getTargetFlightId());
        order.setFlightStatus(FlightStatus.CHANGED);
        orderRepository.save(order);

        return FlightChangeResultModel.builder().changedFlightId(flightChangeModel.getTargetFlightId()).build();
    }

    @Transactional
    public InvoiceResultModel invoice(InvoiceModel invoiceModel) {
        Optional<OrderEntity> orderOptional = orderRepository.findById(invoiceModel.getOrderId());
        OrderEntity order = orderOptional.orElseThrow(() -> new OrderNotFoundException());

        InvoiceMessage invoiceMessage = getInvoiceMessage(invoiceModel, order);

        if (!invoiceMessageSender.send(invoiceMessage)) {
            throw new MessageServiceNotAvailableException();
        }

        order.setStatus(OrderStatus.INVOICED);
        orderRepository.save(order);

        return InvoiceResultModel.builder().InvoicedOrderId(order.getId()).build();
    }

    private InvoiceMessage getInvoiceMessage(InvoiceModel invoiceModel, OrderEntity order) {
        InvoiceMessage invoiceMessage = InvoiceMessage.builder().invoiceTitle(invoiceModel.getTitle()).amount(order.getAmount()).email(invoiceModel.getEmail()).build();
        return invoiceMessage;
    }


    private FlightChangeRequestFeignDto getSeatChangeRequestFeignDto(FlightChangeModel flightChangeModel) {
        return FlightChangeRequestFeignDto.builder()
                .originalFlightId(flightChangeModel.getOriginalFlightId())
                .targetFlightId(flightChangeModel.getTargetFlightId()).build();
    }
}
