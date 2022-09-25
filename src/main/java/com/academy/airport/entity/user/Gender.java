package com.academy.airport.entity.user;

import java.util.Arrays;
import java.util.Optional;

public enum Gender {
    MALE,
    FEMALE;

    public static Optional<Gender> find(final String gender) {
        return Arrays.stream(values())
                .filter(it -> it.name().equals(gender))
                .findFirst();
    }
}
