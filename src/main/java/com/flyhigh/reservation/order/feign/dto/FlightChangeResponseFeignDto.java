package com.flyhigh.reservation.order.feign.dto;

import com.flyhigh.reservation.order.enums.FlightChangeCode;
import com.flyhigh.reservation.order.enums.FlightChangeStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class FlightChangeResponseFeignDto {
    private FlightChangeStatus status;
    private FlightChangeCode code;
    private String message;
}


