package com.flyhigh.reservation.order.feign;

import com.flyhigh.reservation.order.feign.dto.FlightChangeRequestFeignDto;
import com.flyhigh.reservation.order.feign.dto.FlightChangeResponseFeignDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.concurrent.TimeoutException;

@FeignClient(name = "flightPriceSeatCalculation", url = "${feign.flightPriceSeatCalculation.url}")
public interface FlightPriceSeatCalculationFeignClient {

    @PostMapping("/flights/seatChange")
    FlightChangeResponseFeignDto changeSeat(@RequestBody FlightChangeRequestFeignDto flightChangeRequestFeignDto) throws TimeoutException;

}
