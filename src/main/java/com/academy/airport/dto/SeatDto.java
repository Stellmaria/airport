package com.academy.airport.dto;

import com.academy.airport.entity.Seat;
import lombok.Data;

import java.io.Serializable;

/**
 * A DTO for the {@link Seat} entity
 */
@Data
public class SeatDto implements Serializable {
    private final Integer airplaneId;
    private final String seatNo;
    private final AirplaneDto airplaneByAirplaneId;
}