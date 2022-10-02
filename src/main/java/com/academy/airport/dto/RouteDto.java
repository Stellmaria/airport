package com.academy.airport.dto;

import com.academy.airport.entity.Route;
import lombok.Data;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Collection;

/**
 * A DTO for the {@link Route} entity
 */
@Data
public class RouteDto implements Serializable {
    private final Long id;
    private final Timestamp departureDate;
    private final String departureAirportCode;
    private final Timestamp arrivalDate;
    private final String arrivalAirportCode;
    private final Integer airplaneId;
    private final String status;
    private final AirportDto airportByDepartureAirportCode;
    private final AirportDto airportByArrivalAirportCode;
    private final AirplaneDto airplaneByAirplaneId;
    private final Collection<TicketDto> ticketsById;
}