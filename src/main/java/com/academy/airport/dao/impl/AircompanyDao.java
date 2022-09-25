package com.academy.airport.dao.impl;

import com.academy.airport.dao.Dao;
import com.academy.airport.entity.airport.Aircompany;
import com.academy.airport.util.ConnectionManager;
import com.academy.airport.util.SqlHelper;
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
public class AircompanyDao implements Dao<Integer, Aircompany> {
    private static final AircompanyDao INSTANCE = new AircompanyDao();
    @Language("PostgreSQL")
    private static final String SAVE_SQL = "INSERT INTO airport_storage.aircompany(name) "
            + "VALUES (?);";
    @Language("PostgreSQL")
    private static final String UPDATE_SQL = "UPDATE airport_storage.aircompany "
            + "SET name = ? "
            + "WHERE id = ?;";
    @Language("PostgreSQL")
    private static final String FIND_ALL_SQL = "SELECT id, name "
            + "FROM airport_storage.aircompany ";
    @Language("PostgreSQL")
    private static final String FIND_BY_ID_SQL = FIND_ALL_SQL + "WHERE id = ?;";

    @Override
    @SneakyThrows
    public Aircompany save(final @NotNull Aircompany entity) {
        try (var connection = ConnectionManager.get();
             var prepareStatement = connection.prepareStatement(SAVE_SQL, RETURN_GENERATED_KEYS)) {
            prepareStatement.setObject(1, entity.getName());

            prepareStatement.executeUpdate();

            var generatedKeys = prepareStatement.getGeneratedKeys();
            if (generatedKeys.next()) {
                entity.setId(generatedKeys.getInt("id"));
            }
            return entity;
        }
    }

    @Override
    @SneakyThrows
    public void update(final @NotNull Aircompany entity) {
        try (var connection = ConnectionManager.get();
             var prepareStatement = connection.prepareStatement(UPDATE_SQL)) {
            prepareStatement.setObject(1, entity.getName());
            prepareStatement.setObject(2, entity.getId());

            prepareStatement.executeUpdate();
        }
    }

    @Override
    @SneakyThrows
    public boolean delete(final Integer id) {
        try (var connection = ConnectionManager.get();
             var prepareStatement = connection.prepareStatement(
                     SqlHelper.getDeleteSql("aircompany"))) {
            prepareStatement.setInt(1, id);

            return prepareStatement.executeUpdate() > 0;
        }
    }

    @Override
    @SneakyThrows
    public Optional<Aircompany> findById(final Integer id) {
        try (var connection = ConnectionManager.get();
             var prepareStatement = connection.prepareStatement(FIND_BY_ID_SQL)) {
            prepareStatement.setInt(1, id);

            var resultSet = prepareStatement.executeQuery();
            Aircompany aircompany = null;
            if (resultSet.next()) {
                aircompany = buildAircompany(resultSet);
            }
            return Optional.ofNullable(aircompany);
        }
    }

    @Override
    @SneakyThrows
    public List<Aircompany> findAll() {
        try (var connection = ConnectionManager.get();
             var prepareStatement = connection.prepareStatement(FIND_ALL_SQL)) {
            var resultSet = prepareStatement.executeQuery();

            List<Aircompany> aircompanyList = new ArrayList<>();
            while (resultSet.next()) {
                aircompanyList.add(buildAircompany(resultSet));
            }
            return aircompanyList;
        }
    }

    public static AircompanyDao getInstance() {
        return INSTANCE;
    }

    @SneakyThrows
    private Aircompany buildAircompany(@NotNull ResultSet resultSet) {
        return Aircompany.builder()
                .id(resultSet.getInt("id"))
                .name(resultSet.getString("name"))
                .build();
    }
}
