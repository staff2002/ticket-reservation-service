package com.flyhigh.order.ticket.feign;

import com.flyhigh.order.ticket.feign.dto.FlightChangeRequestFeignDto;
import com.flyhigh.order.ticket.feign.dto.FlightChangeResponseFeignDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.concurrent.TimeoutException;

@FeignClient(name = "flightPriceSeatCalculation", url = "${feign.flightPriceSeatCalculation.url}")
public interface FlightPriceSeatCalculationFeignClient {

    @PostMapping("/flights/seatChange")
    FlightChangeResponseFeignDto changeSeat(@RequestBody FlightChangeRequestFeignDto flightChangeRequestFeignDto) throws TimeoutException;

}
