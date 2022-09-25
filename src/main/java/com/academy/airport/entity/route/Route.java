package com.academy.airport.entity.route;

import com.academy.airport.entity.airport.Airplane;
import com.academy.airport.entity.airport.Airport;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Route {
    private Long id;
    private LocalDateTime departureDate;
    private Airport departureAirportCode;
    private LocalDateTime arrivalDate;
    private Airport arrivalAirportCode;
    private Airplane airplane;
    private FlightStatus status;
}
