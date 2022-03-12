package com.flyhigh.order.ticket.service;

import com.flyhigh.order.ticket.enums.OrderStatus;
import com.flyhigh.order.ticket.enums.FlightChangeCode;
import com.flyhigh.order.ticket.enums.FlightChangeStatus;
import com.flyhigh.order.ticket.enums.FlightStatus;
import com.flyhigh.order.ticket.feign.dto.FlightChangeRequestFeignDto;
import com.flyhigh.order.ticket.feign.dto.FlightChangeResponseFeignDto;
import com.flyhigh.order.ticket.feign.FlightPriceSeatCalculationFeign;
import com.flyhigh.order.ticket.mq.InvoiceMessageSender;
import com.flyhigh.order.ticket.mq.messages.InvoiceMessage;
import com.flyhigh.order.ticket.repository.OrderRepository;
import com.flyhigh.order.ticket.repository.entity.OrderEntity;
import com.flyhigh.order.ticket.service.exception.MessageServiceNotAvailableException;
import com.flyhigh.order.ticket.service.exception.NotEnoughSeatException;
import com.flyhigh.order.ticket.service.exception.OrderNotFoundException;
import com.flyhigh.order.ticket.service.model.FlightChangeModel;
import com.flyhigh.order.ticket.service.model.FlightChangeResultModel;
import com.flyhigh.order.ticket.service.model.InvoiceModel;
import com.flyhigh.order.ticket.service.model.InvoiceResultModel;
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

        if(!invoiceMessageSender.send(invoiceMessage)){
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
