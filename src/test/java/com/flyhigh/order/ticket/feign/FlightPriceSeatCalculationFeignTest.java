package com.flyhigh.order.ticket.feign;

import com.flyhigh.order.ticket.enums.FlightChangeStatus;
import com.flyhigh.order.ticket.feign.dto.FlightChangeResponseFeignDto;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.concurrent.TimeoutException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class FlightPriceSeatCalculationFeignTest {

    @Test
    void should_flight_change_successfully_given_price_seat_calculation_system_change_successfully() throws TimeoutException {
        FlightPriceSeatCalculationFeignClient flightPriceSeatCalculationFeignClient = Mockito.mock(FlightPriceSeatCalculationFeignClient.class);
        when(flightPriceSeatCalculationFeignClient.changeSeat(any())).thenReturn(FlightChangeResponseFeignDto.builder().status(FlightChangeStatus.SUCCESS).build());

        FlightPriceSeatCalculationFeign flightPriceSeatCalculationFeign = new FlightPriceSeatCalculationFeign(flightPriceSeatCalculationFeignClient);
        FlightChangeResponseFeignDto flightChangeResponseFeignDto = flightPriceSeatCalculationFeign.changeSeat(any());
        assertEquals(FlightChangeStatus.SUCCESS, flightChangeResponseFeignDto.getStatus());
    }
}
