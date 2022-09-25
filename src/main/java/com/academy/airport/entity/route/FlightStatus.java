package com.academy.airport.entity.route;

import java.util.Arrays;
import java.util.Optional;

public enum FlightStatus {
    ARRIVED,
    DEPARTED,
    CANCELLED,
    SCHEDULED;

    public static Optional<FlightStatus> find(final String status) {
        return Arrays.stream(values())
                .filter(it -> it.name().equals(status))
                .findFirst();
    }
}
