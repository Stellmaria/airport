package com.academy.airport.dao.impl;

import com.academy.airport.dao.Dao;
import com.academy.airport.entity.airport.Airport;
import com.academy.airport.entity.route.City;
import com.academy.airport.util.ConnectionManager;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
import org.intellij.lang.annotations.Language;
import org.jetbrains.annotations.NotNull;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static java.sql.Statement.RETURN_GENERATED_KEYS;
import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
public class AirportDao implements Dao<String, Airport> {
    private static final AirportDao INSTANCE = new AirportDao();
    @Language("PostgreSQL")
    private static final String DELETE_SQL = "DELETE "
            + "FROM airport_storage.airport "
            + "WHERE code = ?;";
    @Language("PostgreSQL")
    private static final String SAVE_SQL = "INSERT INTO airport_storage.airport(code, city_id) "
            + "VALUES (?, ?);";
    @Language("PostgreSQL")
    private static final String UPDATE_SQL = "UPDATE airport_storage.airport "
            + "SET city_id = ? "
            + "WHERE code = ?;";
    @Language("PostgreSQL")
    private static final String FIND_ALL_SQL = "SELECT code, city_id "
            + "FROM airport_storage.airport ";
    @Language("PostgreSQL")
    private static final String FIND_BY_ID_SQL = FIND_ALL_SQL + "WHERE code = ?;";

    @Override
    @SneakyThrows
    public Airport save(final @NotNull Airport entity) {
        try (var connection = ConnectionManager.get();
             var prepareStatement = connection.prepareStatement(SAVE_SQL, RETURN_GENERATED_KEYS)) {
            prepareStatement.setObject(1, entity.getCode().toUpperCase());
            prepareStatement.setObject(2, entity.getCity().getId());

            prepareStatement.executeUpdate();

            var generatedKeys = prepareStatement.getGeneratedKeys();
            if (generatedKeys.next()) {
                entity.setCode(generatedKeys.getString("code"));
            }

            return entity;
        }
    }

    @Override
    @SneakyThrows
    public void update(final @NotNull Airport entity) {
        try (var connection = ConnectionManager.get();
             var prepareStatement = connection.prepareStatement(UPDATE_SQL)) {
            prepareStatement.setObject(1, entity.getCity().getId());
            prepareStatement.setObject(2, entity.getCode());

            prepareStatement.executeUpdate();
        }
    }

    @Override
    @SneakyThrows
    public boolean delete(final String id) {
        try (var connection = ConnectionManager.get();
             var prepareStatement = connection.prepareStatement(DELETE_SQL)) {
            prepareStatement.setObject(1, id);
            return prepareStatement.executeUpdate() > 0;
        }
    }

    @Override
    @SneakyThrows
    public Optional<Airport> findById(final String id) {
        try (var connection = ConnectionManager.get();
             var prepareStatement = connection.prepareStatement(FIND_BY_ID_SQL)) {
            prepareStatement.setObject(1, id);

            var resultSet = prepareStatement.executeQuery();
            Airport airport = null;
            if (resultSet.next()) {
                airport = buildAirport(resultSet);
            }
            return Optional.ofNullable(airport);
        }
    }

    @Override
    @SneakyThrows
    public List<Airport> findAll() {
        try (var connection = ConnectionManager.get();
             var prepareStatement = connection.prepareStatement(FIND_ALL_SQL)) {
            var resultSet = prepareStatement.executeQuery();

            List<Airport> airplaneList = new ArrayList<>();
            while (resultSet.next()) {
                airplaneList.add(buildAirport(resultSet));
            }
            return airplaneList;
        }
    }

    @SneakyThrows
    private Airport buildAirport(@NotNull ResultSet resultSet) {
        return Airport.builder()
                .code(resultSet.getObject("code", String.class))
                .city(City.builder()
                        .id(resultSet.getObject("city_id", Integer.class))
                        .build())
                .build();
    }

    public static AirportDao getInstance() {
        return INSTANCE;
    }
}
