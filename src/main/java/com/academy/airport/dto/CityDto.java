package com.academy.airport.dto;

import com.academy.airport.entity.City;
import lombok.Data;

import java.io.Serializable;
import java.util.Collection;

/**
 * A DTO for the {@link City} entity
 */
@Data
public class CityDto implements Serializable {
    private final Integer id;
    private final Integer countryId;
    private final String name;
    private final Collection<AirportDto> airportsById;
    private final CountryDto countryByCountryId;
}