package com.flyhigh.order.ticket.feign;

import com.flyhigh.order.ticket.feign.dto.FlightChangeRequestFeignDto;
import com.flyhigh.order.ticket.feign.dto.FlightChangeResponseFeignDto;
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

