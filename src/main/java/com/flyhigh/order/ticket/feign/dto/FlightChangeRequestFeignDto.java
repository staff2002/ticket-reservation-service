package com.flyhigh.order.ticket.feign.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class FlightChangeRequestFeignDto {
    private Long originalFlightId;
    private Long targetFlightId;
}
