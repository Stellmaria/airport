package com.academy.airport.dao.impl;

import com.academy.airport.dao.Dao;
import com.academy.airport.entity.Airplane;
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
public class AirplaneDao implements Dao<Integer, Airplane> {
    private static final AirplaneDao INSTANCE = new AirplaneDao();

    @Language("PostgreSQL")
    private static final String DELETE_SQL = "DELETE FROM airport_storage.airplane WHERE id = ?;";
    @Language("PostgreSQL")
    private static final String SAVE_SQL = "INSERT INTO airport_storage.airplane(model, aircompany_id) VALUES (?, ?);";
    @Language("PostgreSQL")
    private static final String UPDATE_SQL = "UPDATE airport_storage.airplane SET model = ?, aircompany_id = ? WHERE id = ?;";
    @Language("PostgreSQL")
    private static final String FIND_ALL_SQL = "SELECT id, model, aircompany_id FROM airport_storage.airplane";
    @Language("PostgreSQL")
    private static final String FIND_BY_ID_SQL = FIND_ALL_SQL + " WHERE id = ?;";

    @Override
    @SneakyThrows
    public Airplane save(final @NotNull Airplane entity) {
        try (var connection = ConnectionManager.get();
             var statement = connection.prepareStatement(SAVE_SQL, RETURN_GENERATED_KEYS)) {
            statement.setString(1, entity.getModel());
            statement.setObject(2, entity.getAircompanyId());
            statement.executeUpdate();
            try (var generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    entity.setId(generatedKeys.getObject("id", Integer.class));
                }
            }
            return entity;
        }
    }

    @Override
    @SneakyThrows
    public void update(final @NotNull Airplane entity) {
        try (var connection = ConnectionManager.get();
             var statement = connection.prepareStatement(UPDATE_SQL)) {
            statement.setString(1, entity.getModel());
            statement.setObject(2, entity.getAircompanyId());
            statement.setObject(3, entity.getId());
            statement.executeUpdate();
        }
    }

    @Override
    @SneakyThrows
    public boolean delete(final Integer id) {
        try (var connection = ConnectionManager.get();
             var statement = connection.prepareStatement(DELETE_SQL)) {
            statement.setObject(1, id);
            return statement.executeUpdate() > 0;
        }
    }

    @Override
    @SneakyThrows
    public Optional<Airplane> findById(final Integer id) {
        try (var connection = ConnectionManager.get()) {
            return findById(id, connection);
        }
    }

    @Override
    @SneakyThrows
    public Optional<Airplane> findById(final Integer id, final Connection connection) {
        try (var statement = connection.prepareStatement(FIND_BY_ID_SQL)) {
            statement.setObject(1, id);
            try (var resultSet = statement.executeQuery()) {
                return resultSet.next() ? Optional.of(buildAirplane(resultSet)) : Optional.empty();
            }
        }
    }

    @Override
    @SneakyThrows
    public List<Airplane> findAll() {
        try (var connection = ConnectionManager.get();
             var statement = connection.prepareStatement(FIND_ALL_SQL);
             var resultSet = statement.executeQuery()) {
            List<Airplane> airplanes = new ArrayList<>();
            while (resultSet.next()) {
                airplanes.add(buildAirplane(resultSet));
            }
            return airplanes;
        }
    }

    @SneakyThrows
    private Airplane buildAirplane(final ResultSet resultSet) {
        return Airplane.builder()
                .id(resultSet.getObject("id", Integer.class))
                .model(resultSet.getString("model"))
                .aircompanyId(resultSet.getObject("aircompany_id", Integer.class))
                .build();
    }

    public static AirplaneDao getInstance() {
        return INSTANCE;
    }
}
