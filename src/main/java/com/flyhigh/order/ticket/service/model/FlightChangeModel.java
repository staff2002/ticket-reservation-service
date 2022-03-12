package com.flyhigh.order.ticket.service.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FlightChangeModel {
    private Long orderId;
    private Long originalFlightId;
    private Long targetFlightId;
}
