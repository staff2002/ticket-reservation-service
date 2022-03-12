package com.flyhigh.reservation.order.service.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FlightChangeModel {
    private Long orderId;
    private Long originalFlightId;
    private Long targetFlightId;
}
