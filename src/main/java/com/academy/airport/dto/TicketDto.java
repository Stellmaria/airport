package com.academy.airport.dto;

import com.academy.airport.entity.Ticket;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * A DTO for the {@link Ticket} entity
 */
@Data
public class TicketDto implements Serializable {
    private final Long id;
    private final Integer userId;
    private final Integer routeId;
    private final String seatNo;
    private final BigDecimal cost;
    private final UserDto userByUserId;
    private final RouteDto routeByRouteId;
}