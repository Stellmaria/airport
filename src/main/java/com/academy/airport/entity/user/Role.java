package com.academy.airport.entity.user;

import java.util.Arrays;
import java.util.Optional;

public enum Role {
    USER,
    ADMIN;

    public static Optional<Role> find(final String role) {
        return Arrays.stream(values())
                .filter(it -> it.name().equals(role))
                .findFirst();
    }
}
