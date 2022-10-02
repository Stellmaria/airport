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
    private static final String DELETE_SQL = """
            DELETE
            FROM airport_storage.airplane
            WHERE id = ?;""";
    @Language("PostgreSQL")
    private static final String SAVE_SQL = """
            INSERT INTO airport_storage.airplane(model, aircompany_id)
            VALUES (?, ?);""";
    @Language("PostgreSQL")
    private static final String UPDATE_SQL = """
            UPDATE airport_storage.airplane
            SET model         = ?,
                aircompany_id = ?
            WHERE id = ?;""";
    @Language("PostgreSQL")
    private static final String FIND_ALL_SQL = """
            SELECT id,
                   model,
                   aircompany_id
            FROM airport_storage.airplane""";
    @Language("PostgreSQL")
    private static final String FIND_BY_ID_SQL = FIND_ALL_SQL + " WHERE id = ?;";

    @Override
    @SneakyThrows
    public Airplane save(final @NotNull Airplane entity) {
        try (var connection = ConnectionManager.get();
             var prepareStatement = connection.prepareStatement(SAVE_SQL, RETURN_GENERATED_KEYS)) {
            prepareStatement.setObject(1, entity.getModel());
            prepareStatement.setObject(2, entity.getAircompanyId());
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
    public void update(final @NotNull Airplane entity) {
        try (var connection = ConnectionManager.get();
             var prepareStatement = connection.prepareStatement(UPDATE_SQL)) {
            prepareStatement.setObject(1, entity.getModel());
            prepareStatement.setObject(2, entity.getAircompanyId());
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
    public Optional<Airplane> findById(final Integer id) {
        try (var connection = ConnectionManager.get();
             var prepareStatement = connection.prepareStatement(FIND_BY_ID_SQL)) {
            prepareStatement.setObject(1, id);
            var resultSet = prepareStatement.executeQuery();
            Airplane airplane = null;
            if (resultSet.next()) {
                airplane = buildAirplane(resultSet);
            }
            return Optional.ofNullable(airplane);
        }
    }

    @Override
    public Optional<Airplane> findById(Integer id, Connection connection) {
        return Optional.empty();
    }

    @Override
    @SneakyThrows
    public List<Airplane> findAll() {
        try (var connection = ConnectionManager.get();
             var prepareStatement = connection.prepareStatement(FIND_ALL_SQL)) {
            var resultSet = prepareStatement.executeQuery();
            List<Airplane> airplaneList = new ArrayList<>();
            while (resultSet.next()) {
                airplaneList.add(buildAirplane(resultSet));
            }
            return airplaneList;
        }
    }

    @SneakyThrows
    private Airplane buildAirplane(@NotNull ResultSet resultSet) {
        return Airplane.builder()
                .id(resultSet.getObject("id", Integer.class))
                .model(resultSet.getObject("model", String.class))
                .aircompanyId(resultSet.getObject("aircompany_id", Integer.class))
                .build();
    }

    public static AirplaneDao getInstance() {
        return INSTANCE;
    }
}
