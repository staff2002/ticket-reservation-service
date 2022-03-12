package com.flyhigh.reservation.order.feign;

import com.flyhigh.reservation.order.feign.dto.FlightChangeRequestFeignDto;
import com.flyhigh.reservation.order.feign.dto.FlightChangeResponseFeignDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeoutException;

@Service
@RequiredArgsConstructor
public class FlightPriceSeatCalculationFeign {

    final FlightPriceSeatCalculationFeignClient flightPriceSeatCalculationFeignClient;

    public FlightChangeResponseFeignDto changeSeat(FlightChangeRequestFeignDto flightChangeRequestFeignDto) throws TimeoutException {
        return flightPriceSeatCalculationFeignClient.changeSeat(flightChangeRequestFeignDto);
    }
}

