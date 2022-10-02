package com.academy.airport.dao.impl;

import com.academy.airport.dao.Dao;
import com.academy.airport.entity.Country;
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
public class CountryDao implements Dao<Integer, Country> {
    private static final CountryDao INSTANCE = new CountryDao();
    @Language("PostgreSQL")
    private static final String DELETE_SQL = "DELETE "
            + "FROM airport_storage.country "
            + "WHERE id = ?;";
    @Language("PostgreSQL")
    private static final String SAVE_SQL = "INSERT INTO airport_storage.country(name)"
            + "VALUES (?);";

    @Override
    @SneakyThrows
    public Country save(final @NotNull Country entity) {
        try (var connection = ConnectionManager.get();
             var prepareStatement = connection.prepareStatement(SAVE_SQL, RETURN_GENERATED_KEYS)) {
            prepareStatement.setObject(1, entity.getName());

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
    public void update(final Country entity) {

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
    public Optional<Country> findById(final Integer id) {
        return Optional.empty();
    }

    @Override
    public Optional<Country> findById(Integer id, Connection connection) {
        return Optional.empty();
    }

    @Override
    @SneakyThrows
    public List<Country> findAll() {
        return null;
    }

    public static CountryDao getInstance() {
        return INSTANCE;
    }
}
