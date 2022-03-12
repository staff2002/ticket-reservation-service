package com.flyhigh.order.ticket.feign.dto;

import com.flyhigh.order.ticket.enums.FlightChangeCode;
import com.flyhigh.order.ticket.enums.FlightChangeStatus;
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


