package com.flyhigh.reservation.order.feign;

import com.flyhigh.reservation.order.enums.FlightChangeCode;
import com.flyhigh.reservation.order.enums.FlightChangeStatus;
import com.flyhigh.reservation.order.feign.dto.FlightChangeResponseFeignDto;
import com.flyhigh.reservation.order.service.exception.NotEnoughSeatException;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.concurrent.TimeoutException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
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

    @Test
    void should_flight_change_failed_given_price_seat_calculation_system_time_out() throws TimeoutException {
        FlightPriceSeatCalculationFeignClient flightPriceSeatCalculationFeignClient = Mockito.mock(FlightPriceSeatCalculationFeignClient.class);
        when(flightPriceSeatCalculationFeignClient.changeSeat(any())).thenThrow(new TimeoutException());

        FlightPriceSeatCalculationFeign flightPriceSeatCalculationFeign = new FlightPriceSeatCalculationFeign(flightPriceSeatCalculationFeignClient);

        assertThrows(TimeoutException.class,() -> flightPriceSeatCalculationFeign.changeSeat(any()));
    }

    @Test
    void should_flight_change_failed_given_not_enough_seat() throws TimeoutException {
        FlightPriceSeatCalculationFeignClient flightPriceSeatCalculationFeignClient = Mockito.mock(FlightPriceSeatCalculationFeignClient.class);
        when(flightPriceSeatCalculationFeignClient.changeSeat(any())).thenReturn(FlightChangeResponseFeignDto.builder()
                .status(FlightChangeStatus.FAILED).code(FlightChangeCode.NO_SEAT).build());

        FlightPriceSeatCalculationFeign flightPriceSeatCalculationFeign = new FlightPriceSeatCalculationFeign(flightPriceSeatCalculationFeignClient);
        FlightChangeResponseFeignDto flightChangeResponseFeignDto = flightPriceSeatCalculationFeign.changeSeat(any());

        assertEquals(FlightChangeCode.NO_SEAT, flightChangeResponseFeignDto.getCode());
        assertEquals(FlightChangeStatus.FAILED, flightChangeResponseFeignDto.getStatus());
    }
}
