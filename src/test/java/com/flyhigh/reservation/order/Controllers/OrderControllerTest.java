package com.flyhigh.reservation.order.Controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.flyhigh.reservation.order.bases.TestBase;
import com.flyhigh.reservation.order.controller.dto.FlightChangeRequestDto;
import com.flyhigh.reservation.order.controller.dto.InvoiceRequestDto;
import com.flyhigh.reservation.order.repository.OrderRepository;
import com.flyhigh.reservation.order.service.OrderService;
import com.flyhigh.reservation.order.service.exception.MessageServiceNotAvailableException;
import com.flyhigh.reservation.order.service.exception.NotEnoughSeatException;
import com.flyhigh.reservation.order.service.exception.OrderNotFoundException;
import com.flyhigh.reservation.order.service.model.FlightChangeResultModel;
import com.flyhigh.reservation.order.service.model.InvoiceResultModel;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.concurrent.TimeoutException;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class OrderControllerTest extends TestBase {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    OrderRepository orderRepository;

    @MockBean
    OrderService orderService;

    @Test
    public void should_change_flight_successfully_given_price_seat_calculation_system_change_successfully() throws Exception {
        Mockito.when(orderService.change(any()))
                .thenReturn(FlightChangeResultModel.builder().changedFlightId(any()).build());

        FlightChangeRequestDto request = FlightChangeRequestDto.builder().targetFlightId(2L).build();
        String requestJson = objectMapper.writeValueAsString(request);


        mockMvc.perform(post("/orders/1/flights/1/change")
                .content(requestJson)
                .contentType(MediaType.APPLICATION_JSON))
               .andExpect(status().isOk());
    }

    @Test
    public void should_change_flight_fail_given_not_enough_seat() throws Exception {
        Mockito.when(orderService.change(any()))
                .thenThrow(new NotEnoughSeatException());

        FlightChangeRequestDto request = FlightChangeRequestDto.builder().targetFlightId(2L).build();
        String requestJson = objectMapper.writeValueAsString(request);


        mockMvc.perform(post("/orders/1/flights/1/change")
                        .content(requestJson)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.errorCode", is("NO_SEAT")))
                .andExpect(jsonPath("$.message", is("没有足够座位，改签失败")));;
    }

    @Test
    public void should_change_flight_fail_given_price_seat_calculation_system_not_available() throws Exception {
        Mockito.when(orderService.change(any()))
                .thenThrow(new TimeoutException());

        FlightChangeRequestDto request = FlightChangeRequestDto.builder().targetFlightId(2L).build();
        String requestJson = objectMapper.writeValueAsString(request);


        mockMvc.perform(post("/orders/1/flights/1/change")
                        .content(requestJson)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isServiceUnavailable())
                .andExpect(jsonPath("$.errorCode", is("SYSTEM_IS_UNAVAILABLE")))
                .andExpect(jsonPath("$.message", is("改签失败")));;
    }

    @Test
    public void should_invoice_successfully_given_order_existed() throws Exception {
        Mockito.when(orderService.invoice(any()))
                .thenReturn(InvoiceResultModel.builder().InvoicedOrderId(1L).build());

        InvoiceRequestDto invoiceRequestDto = InvoiceRequestDto.builder().invoiceTitle("李四").email("lisi@163.com").build();

        String requestJson = objectMapper.writeValueAsString(invoiceRequestDto);


        mockMvc.perform(post("/orders/1/invoice")
                        .content(requestJson)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void should_invoice_failed_given_order_not_exist() throws Exception {
        Mockito.when(orderService.invoice(any()))
                .thenThrow(new OrderNotFoundException());

        InvoiceRequestDto invoiceRequestDto = InvoiceRequestDto.builder().invoiceTitle("李四").email("lisi@163.com").build();

        String requestJson = objectMapper.writeValueAsString(invoiceRequestDto);


        mockMvc.perform(post("/orders/1/invoice")
                        .content(requestJson)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.errorCode", is("ORDER_NOT_FOUND")))
                .andExpect(jsonPath("$.message", is("找不到该订单，申请开票失败")));
    }

    @Test
    public void should_invoice_failed_given_message_service_not_available() throws Exception {
        Mockito.when(orderService.invoice(any()))
                .thenThrow(new MessageServiceNotAvailableException());

        InvoiceRequestDto invoiceRequestDto = InvoiceRequestDto.builder().invoiceTitle("李四").email("lisi@163.com").build();

        String requestJson = objectMapper.writeValueAsString(invoiceRequestDto);


        mockMvc.perform(post("/orders/1/invoice")
                        .content(requestJson)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.errorCode", is("SYSTEM_IS_UNAVAILABLE")))
                .andExpect(jsonPath("$.message", is("申请开票失败")));
    }
}
