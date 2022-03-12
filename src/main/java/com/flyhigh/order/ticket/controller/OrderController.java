package com.flyhigh.order.ticket.controller;

import com.flyhigh.order.ticket.controller.dto.FlightChangeRequestDto;
import com.flyhigh.order.ticket.controller.dto.ResultResponseDto;
import com.flyhigh.order.ticket.controller.dto.InvoiceRequestDto;
import com.flyhigh.order.ticket.service.OrderService;
import com.flyhigh.order.ticket.service.exception.MessageServiceNotAvailableException;
import com.flyhigh.order.ticket.service.exception.NotEnoughSeatException;
import com.flyhigh.order.ticket.service.exception.OrderNotFoundException;
import com.flyhigh.order.ticket.service.model.FlightChangeModel;
import com.flyhigh.order.ticket.service.model.InvoiceModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.TimeoutException;

@RestController
@RequestMapping("/orders")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @PostMapping("/{id}/flights/{flightId}/change")
    public ResponseEntity<ResultResponseDto> changeFlight(@PathVariable Long id, @PathVariable Long flightId, @RequestBody FlightChangeRequestDto request) {
        FlightChangeModel flightChangeModel = FlightChangeModel.builder()
                .orderId(id)
                .originalFlightId(flightId)
                .targetFlightId(request.getTargetFlightId())
                .build();

        try {
            orderService.change(flightChangeModel);
            return new ResponseEntity<>(ResultResponseDto.builder().build(), HttpStatus.OK);
        } catch (TimeoutException e) {
            return getErrorResponseEntity("SYSTEM_IS_UNAVAILABLE","改签失败",HttpStatus.SERVICE_UNAVAILABLE);
        } catch (NotEnoughSeatException e) {
            return getErrorResponseEntity("NO_SEAT","没有足够座位，改签失败",HttpStatus.CONFLICT);
        }

    }

    @PostMapping("/{id}/invoice")
    public ResponseEntity<ResultResponseDto> invoice(@PathVariable Long id, @RequestBody InvoiceRequestDto request){

        InvoiceModel invoiceModel = InvoiceModel.builder()
                .orderId(id)
                .email(request.getEmail())
                .title(request.getInvoiceTitle()).build();

        try {
            orderService.invoice(invoiceModel);
            return new ResponseEntity<>(ResultResponseDto.builder().build(), HttpStatus.OK);
        }catch(OrderNotFoundException e){
            return getErrorResponseEntity("ORDER_NOT_FOUND","找不到该订单，申请开票失败",HttpStatus.CONFLICT);
        }catch(MessageServiceNotAvailableException e){
            return getErrorResponseEntity("SYSTEM_IS_UNAVAILABLE","申请开票失败",HttpStatus.CONFLICT);
        }
    }

    private ResponseEntity<ResultResponseDto> getErrorResponseEntity(String errorCode, String message, HttpStatus httpStatus) {
        return new ResponseEntity<>(ResultResponseDto.builder()
                .errorCode(errorCode)
                .message(message)
                .build(), httpStatus);
    }
}
