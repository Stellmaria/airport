package com.academy.airport.dao.impl;

import com.academy.airport.dao.Dao;
import com.academy.airport.entity.route.City;
import com.academy.airport.entity.route.Country;
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
public class CityDao implements Dao<Integer, City> {
    private static final CityDao INSTANCE = new CityDao();
    @Language("PostgreSQL")
    private static final String DELETE_SQL = "DELETE "
            + "FROM airport_storage.city "
            + "WHERE id = ?;";
    @Language("PostgreSQL")
    private static final String SAVE_SQL = "INSERT INTO airport_storage.city(country_id, name) "
            + "VALUES (?, ?);";
    @Language("PostgreSQL")
    private static final String UPDATE_SQL = "UPDATE airport_storage.city "
            + "SET country_id = ?, name = ? "
            + "WHERE id = ?;";
    @Language("PostgreSQL")
    private static final String FIND_ALL_SQL = "SELECT id, country_id, name "
            + "FROM airport_storage.city ";
    @Language("PostgreSQL")
    private static final String FIND_BY_ID_SQL = FIND_ALL_SQL + "WHERE id = ?;";

    @Override
    @SneakyThrows
    public City save(final @NotNull City entity) {
        try (var connection = ConnectionManager.get();
             var prepareStatement = connection.prepareStatement(SAVE_SQL, RETURN_GENERATED_KEYS)) {
            prepareStatement.setObject(1, entity.getCountry().getId());
            prepareStatement.setObject(2, entity.getName());

            prepareStatement.executeUpdate();

            var generatedKeys = prepareStatement.getGeneratedKeys();
            if (generatedKeys.next()) {
                entity.setId(generatedKeys.getObject("id", Integer.class));
            }
            return entity;
        }
    }

    @Override
    @SneakyThrows
    public void update(final @NotNull City entity) {
        try (var connection = ConnectionManager.get();
             var prepareStatement = connection.prepareStatement(UPDATE_SQL)) {
            prepareStatement.setObject(1, entity.getCountry().getId());
            prepareStatement.setObject(2, entity.getName());
            prepareStatement.setObject(3, entity.getId());


            prepareStatement.executeUpdate();
        }
    }

    @Override
    @SneakyThrows
    public boolean delete(final Integer id) {
        try (var connection = ConnectionManager.get();
             var prepareStatement = connection.prepareStatement(DELETE_SQL)) {
            prepareStatement.setObject(1, id);
            return prepareStatement.executeUpdate() > 0;
        }
    }

    @Override
    @SneakyThrows
    public Optional<City> findById(Integer id, Connection connection) {
        try (var prepareStatement = connection.prepareStatement(FIND_BY_ID_SQL)) {
            prepareStatement.setObject(1, id);

            var resultSet = prepareStatement.executeQuery();
            City city = null;
            if (resultSet.next()) {
                city = buildCity(resultSet);
            }
            return Optional.ofNullable(city);
        }
    }

    @Override
    @SneakyThrows
    public Optional<City> findById(final Integer id) {
        try (var connection = ConnectionManager.get()) {
            return findById(id, connection);
        }
    }


    @Override
    @SneakyThrows
    public List<City> findAll() {
        try (var connection = ConnectionManager.get();
             var prepareStatement = connection.prepareStatement(FIND_ALL_SQL)) {
            var resultSet = prepareStatement.executeQuery();

            List<City> cities = new ArrayList<>();
            while (resultSet.next()) {
                cities.add(buildCity(resultSet));
            }
            return cities;
        }
    }

    @SneakyThrows
    private City buildCity(@NotNull ResultSet resultSet) {
        return City.builder()
                .id(resultSet.getObject("id", Integer.class))
                .country(Country.builder()
                        .id(resultSet.getObject("country_id", Integer.class))
                        .build())
                .name(resultSet.getObject("name", String.class))
                .build();
    }

    public static CityDao getInstance() {
        return INSTANCE;
    }
}
