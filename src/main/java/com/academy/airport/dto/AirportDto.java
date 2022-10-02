package com.academy.airport.dto;

import com.academy.airport.entity.Airport;
import lombok.Data;

import java.io.Serializable;
import java.util.Collection;

/**
 * A DTO for the {@link Airport} entity
 */
@Data
public class AirportDto implements Serializable {
    private final String code;
    private final Integer cityId;
    private final CityDto cityByCityId;
    private final Collection<RouteDto> routesByCode;
    private final Collection<RouteDto> routesByCode0;
}