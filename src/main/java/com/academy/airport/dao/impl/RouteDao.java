package com.academy.airport.dao.impl;

import com.academy.airport.dao.Dao;
import com.academy.airport.entity.Route;
import com.academy.airport.util.ConnectionManager;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
import org.intellij.lang.annotations.Language;
import org.jetbrains.annotations.NotNull;

import java.sql.Connection;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static java.sql.Statement.RETURN_GENERATED_KEYS;
import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
public class RouteDao implements Dao<Long, Route> {
    private static final RouteDao INSTANCE = new RouteDao();

    @Language("PostgreSQL")
    private static final String DELETE_SQL = "DELETE FROM airport_storage.route WHERE id = ?;";
    @Language("PostgreSQL")
    private static final String SAVE_SQL = """
            INSERT INTO airport_storage.route(
                departure_date, departure_airport_code, arrival_date,
                arrival_airport_code, airplane_id, status
            ) VALUES (?, ?, ?, ?, ?, ?);
            """;
    @Language("PostgreSQL")
    private static final String UPDATE_SQL = """
            UPDATE airport_storage.route
            SET departure_date = ?, departure_airport_code = ?, arrival_date = ?,
                arrival_airport_code = ?, airplane_id = ?, status = ?
            WHERE id = ?;
            """;
    @Language("PostgreSQL")
    private static final String FIND_ALL_SQL = """
            SELECT id, departure_date, departure_airport_code, arrival_date,
                   arrival_airport_code, airplane_id, status
            FROM airport_storage.route
            """;
    @Language("PostgreSQL")
    private static final String FIND_BY_ID_SQL = FIND_ALL_SQL + " WHERE id = ?;";

    @Override
    @SneakyThrows
    public Route save(final @NotNull Route entity) {
        try (var connection = ConnectionManager.get();
             var statement = connection.prepareStatement(SAVE_SQL, RETURN_GENERATED_KEYS)) {
            bindRoute(statement, entity);
            statement.executeUpdate();
            try (var generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    entity.setId(generatedKeys.getObject("id", Long.class));
                }
            }
            return entity;
        }
    }

    @Override
    @SneakyThrows
    public void update(final @NotNull Route entity) {
        try (var connection = ConnectionManager.get();
             var statement = connection.prepareStatement(UPDATE_SQL)) {
            bindRoute(statement, entity);
            statement.setObject(7, entity.getId());
            statement.executeUpdate();
        }
    }

    @Override
    @SneakyThrows
    public boolean delete(final Long id) {
        try (var connection = ConnectionManager.get();
             var statement = connection.prepareStatement(DELETE_SQL)) {
            statement.setObject(1, id);
            return statement.executeUpdate() > 0;
        }
    }

    @Override
    @SneakyThrows
    public Optional<Route> findById(final Long id) {
        try (var connection = ConnectionManager.get()) {
            return findById(id, connection);
        }
    }

    @Override
    @SneakyThrows
    public Optional<Route> findById(final Long id, final Connection connection) {
        try (var statement = connection.prepareStatement(FIND_BY_ID_SQL)) {
            statement.setObject(1, id);
            try (var resultSet = statement.executeQuery()) {
                return resultSet.next() ? Optional.of(buildRoute(resultSet)) : Optional.empty();
            }
        }
    }

    @Override
    @SneakyThrows
    public List<Route> findAll() {
        try (var connection = ConnectionManager.get();
             var statement = connection.prepareStatement(FIND_ALL_SQL);
             var resultSet = statement.executeQuery()) {
            List<Route> routes = new ArrayList<>();
            while (resultSet.next()) {
                routes.add(buildRoute(resultSet));
            }
            return routes;
        }
    }

    @SneakyThrows
    private void bindRoute(final java.sql.PreparedStatement statement, final Route entity) {
        statement.setTimestamp(1, entity.getDepartureDate());
        statement.setString(2, entity.getDepartureAirportCode());
        statement.setTimestamp(3, entity.getArrivalDate());
        statement.setString(4, entity.getArrivalAirportCode());
        statement.setObject(5, entity.getAirplaneId());
        statement.setString(6, entity.getStatus());
    }

    @SneakyThrows
    private Route buildRoute(final ResultSet resultSet) {
        return Route.builder()
                .id(resultSet.getObject("id", Long.class))
                .departureDate(resultSet.getTimestamp("departure_date"))
                .departureAirportCode(resultSet.getString("departure_airport_code").trim())
                .arrivalDate(resultSet.getTimestamp("arrival_date"))
                .arrivalAirportCode(resultSet.getString("arrival_airport_code").trim())
                .airplaneId(resultSet.getObject("airplane_id", Integer.class))
                .status(resultSet.getString("status"))
                .build();
    }

    public static RouteDao getInstance() {
        return INSTANCE;
    }
}
