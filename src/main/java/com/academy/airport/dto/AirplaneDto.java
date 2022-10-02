package com.academy.airport.dto;

import com.academy.airport.entity.Airplane;
import lombok.Data;

import java.io.Serializable;
import java.util.Collection;

/**
 * A DTO for the {@link Airplane} entity
 */
@Data
public class AirplaneDto implements Serializable {
    private final Integer id;
    private final String model;
    private final Integer aircompanyId;
    private final AircompanyDto aircompanyByAircompanyId;
    private final Collection<RouteDto> routesById;
    private final Collection<SeatDto> seatsById;
}