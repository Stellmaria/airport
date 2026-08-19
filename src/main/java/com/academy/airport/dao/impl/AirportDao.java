package com.academy.airport.dao.impl;

import com.academy.airport.dao.Dao;
import com.academy.airport.entity.Airport;
import com.academy.airport.util.ConnectionManager;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
import org.intellij.lang.annotations.Language;
import org.jetbrains.annotations.NotNull;

import java.sql.Connection;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
public class AirportDao implements Dao<String, Airport> {
    private static final AirportDao INSTANCE = new AirportDao();

    @Language("PostgreSQL")
    private static final String DELETE_SQL = "DELETE FROM airport_storage.airport WHERE code = ?;";
    @Language("PostgreSQL")
    private static final String SAVE_SQL = "INSERT INTO airport_storage.airport(code, city_id) VALUES (?, ?);";
    @Language("PostgreSQL")
    private static final String UPDATE_SQL = "UPDATE airport_storage.airport SET city_id = ? WHERE code = ?;";
    @Language("PostgreSQL")
    private static final String FIND_ALL_SQL = "SELECT code, city_id FROM airport_storage.airport";
    @Language("PostgreSQL")
    private static final String FIND_BY_ID_SQL = FIND_ALL_SQL + " WHERE code = ?;";

    @Override
    @SneakyThrows
    public Airport save(final @NotNull Airport entity) {
        var code = normalizeCode(entity.getCode());
        try (var connection = ConnectionManager.get();
             var statement = connection.prepareStatement(SAVE_SQL)) {
            statement.setString(1, code);
            statement.setObject(2, entity.getCityId());
            statement.executeUpdate();
            entity.setCode(code);
            return entity;
        }
    }

    @Override
    @SneakyThrows
    public void update(final @NotNull Airport entity) {
        try (var connection = ConnectionManager.get();
             var statement = connection.prepareStatement(UPDATE_SQL)) {
            statement.setObject(1, entity.getCityId());
            statement.setString(2, normalizeCode(entity.getCode()));
            statement.executeUpdate();
        }
    }

    @Override
    @SneakyThrows
    public boolean delete(final String id) {
        try (var connection = ConnectionManager.get();
             var statement = connection.prepareStatement(DELETE_SQL)) {
            statement.setString(1, normalizeCode(id));
            return statement.executeUpdate() > 0;
        }
    }

    @Override
    @SneakyThrows
    public Optional<Airport> findById(final String id) {
        try (var connection = ConnectionManager.get()) {
            return findById(id, connection);
        }
    }

    @Override
    @SneakyThrows
    public Optional<Airport> findById(final String id, final Connection connection) {
        try (var statement = connection.prepareStatement(FIND_BY_ID_SQL)) {
            statement.setString(1, normalizeCode(id));
            try (var resultSet = statement.executeQuery()) {
                return resultSet.next() ? Optional.of(buildAirport(resultSet)) : Optional.empty();
            }
        }
    }

    @Override
    @SneakyThrows
    public List<Airport> findAll() {
        try (var connection = ConnectionManager.get();
             var statement = connection.prepareStatement(FIND_ALL_SQL);
             var resultSet = statement.executeQuery()) {
            List<Airport> airports = new ArrayList<>();
            while (resultSet.next()) {
                airports.add(buildAirport(resultSet));
            }
            return airports;
        }
    }

    private String normalizeCode(final String code) {
        if (code == null || code.length() != 3) {
            throw new IllegalArgumentException("Airport code must contain exactly 3 characters");
        }
        return code.toUpperCase(Locale.ROOT);
    }

    @SneakyThrows
    private Airport buildAirport(final ResultSet resultSet) {
        return Airport.builder()
                .code(resultSet.getString("code").trim())
                .cityId(resultSet.getObject("city_id", Integer.class))
                .build();
    }

    public static AirportDao getInstance() {
        return INSTANCE;
    }
}
