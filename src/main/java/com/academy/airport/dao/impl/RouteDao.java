package com.academy.airport.dao.impl;

import com.academy.airport.dao.Dao;
import com.academy.airport.entity.Route;
import com.academy.airport.util.ConnectionManager;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
import org.intellij.lang.annotations.Language;
import org.jetbrains.annotations.NotNull;

import java.sql.Connection;
import java.util.List;
import java.util.Optional;

import static java.sql.Statement.RETURN_GENERATED_KEYS;
import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
public class RouteDao implements Dao<Long, Route> {
    private static final RouteDao INSTANCE = new RouteDao();
    @Language("PostgreSQL")
    private static final String DELETE_SQL = "DELETE "
            + "FROM airport_storage.route "
            + "WHERE id = ?;";
    @Language("PostgreSQL")
    private static final String SAVE_SQL = "INSERT INTO airport_storage.route(departure_date, departure_airport_code,"
            + " arrival_date, arrival_airport_code, airplane_id, status) "
            + "VALUES (?, ?, ?, ?, ?, ?);";

    @Override
    @SneakyThrows
    public Route save(final @NotNull Route entity) {
        try (var connection = ConnectionManager.get();
             var prepareStatement = connection.prepareStatement(SAVE_SQL, RETURN_GENERATED_KEYS)) {
            prepareStatement.setObject(1, entity.getDepartureDate());
            prepareStatement.setObject(2, entity.getDepartureAirportCode());
            prepareStatement.setObject(3, entity.getArrivalDate());
            prepareStatement.setObject(4, entity.getArrivalAirportCode());
            prepareStatement.setObject(5, entity.getAirplaneId());
            prepareStatement.setObject(6, entity.getStatus());

            prepareStatement.executeUpdate();

            var generatedKeys = prepareStatement.getGeneratedKeys();
            if (generatedKeys.next()) {
                entity.setId(generatedKeys.getObject("id", Long.class));
            }
            return entity;
        }
    }

    @Override
    @SneakyThrows
    public void update(final Route entity) {

    }

    @Override
    @SneakyThrows
    public boolean delete(final Long id) {
        try (var connection = ConnectionManager.get();
             var prepareStatement = connection.prepareStatement(DELETE_SQL)) {
            prepareStatement.setObject(1, id);
            return prepareStatement.executeUpdate() > 0;
        }
    }

    @Override
    @SneakyThrows
    public Optional<Route> findById(final Long id) {
        return Optional.empty();
    }

    @Override
    public Optional<Route> findById(Long id, Connection connection) {
        return Optional.empty();
    }

    @Override
    @SneakyThrows
    public List<Route> findAll() {
        return null;
    }

    public static RouteDao getInstance() {
        return INSTANCE;
    }
}
