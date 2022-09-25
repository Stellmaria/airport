package com.academy.airport.dao.impl;

import com.academy.airport.dao.Dao;
import com.academy.airport.entity.airport.Airport;
import com.academy.airport.exception.DaoException;
import com.academy.airport.util.ConnectionManager;
import com.academy.airport.util.SqlHelper;
import lombok.NoArgsConstructor;
import org.intellij.lang.annotations.Language;
import org.jetbrains.annotations.NotNull;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

import static java.sql.Statement.RETURN_GENERATED_KEYS;
import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
public class AirportDao implements Dao<String, Airport> {
    private static final AirportDao INSTANCE = new AirportDao();
    @Language("PostgreSQL")
    private static final String SAVE_SQL = "INSERT INTO airport_storage.airport(code, city_id) "
            + "VALUES (?, ?);";

    @Override
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
        } catch (SQLException e) {
            throw new DaoException(e);
        }
    }

    @Override
    public void update(final Airport entity) {

    }

    @Override
    public boolean delete(final String id) {
        try (var connection = ConnectionManager.get();
             var prepareStatement = connection.prepareStatement(
                     SqlHelper.getDeleteSql("airport"))) {
            prepareStatement.setString(1, id);
            return prepareStatement.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DaoException(e);
        }
    }

    @Override
    public Optional<Airport> findById(final String id) {
        return Optional.empty();
    }

    @Override
    public List<Airport> findAll() {
        return null;
    }

    public static AirportDao getInstance() {
        return INSTANCE;
    }
}
