package com.academy.airport.dto;

import com.academy.airport.entity.Country;
import lombok.Data;

import java.io.Serializable;
import java.util.Collection;

/**
 * A DTO for the {@link Country} entity
 */
@Data
public class CountryDto implements Serializable {
    private final Integer id;
    private final String name;
    private final Collection<CityDto> citiesById;
}