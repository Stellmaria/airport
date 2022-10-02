package com.academy.airport.dto;

import com.academy.airport.entity.Aircompany;
import lombok.Data;

import java.io.Serializable;
import java.util.Collection;

/**
 * A DTO for the {@link Aircompany} entity
 */
@Data
public class AircompanyDto implements Serializable {
    private final Integer id;
    private final String name;
    private final Collection<AirplaneDto> airplanesById;
}