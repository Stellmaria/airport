package com.academy.airport.dao.impl;

import com.academy.airport.dao.Dao;
import com.academy.airport.entity.route.Route;
import com.academy.airport.exception.DaoException;
import com.academy.airport.util.ConnectionManager;
import lombok.NoArgsConstructor;
import org.intellij.lang.annotations.Language;
import org.jetbrains.annotations.NotNull;

import java.sql.SQLException;
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
    public Route save(final @NotNull Route entity) {
        try (var connection = ConnectionManager.get();
             var prepareStatement = connection.prepareStatement(SAVE_SQL, RETURN_GENERATED_KEYS)) {
            prepareStatement.setObject(1, entity.getDepartureDate());
            prepareStatement.setObject(2, entity.getDepartureAirportCode().getCode().toUpperCase());
            prepareStatement.setObject(3, entity.getArrivalDate());
            prepareStatement.setObject(4, entity.getArrivalAirportCode().getCode().toUpperCase());
            prepareStatement.setObject(5, entity.getAirplane().getId());
            prepareStatement.setObject(6, entity.getStatus().name().toUpperCase());

            prepareStatement.executeUpdate();

            var generatedKeys = prepareStatement.getGeneratedKeys();
            if (generatedKeys.next()) {
                entity.setId(generatedKeys.getLong("id"));
            }
            return entity;
        } catch (SQLException e) {
            throw new DaoException(e);
        }
    }

    @Override
    public void update(final Route entity) {

    }

    @Override
    public boolean delete(final Long id) {
        try (var connection = ConnectionManager.get();
             var prepareStatement = connection.prepareStatement(DELETE_SQL)) {
            prepareStatement.setLong(1, id);
            return prepareStatement.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DaoException(e);
        }
    }

    @Override
    public Optional<Route> findById(final Long id) {
        return Optional.empty();
    }

    @Override
    public List<Route> findAll() {
        return null;
    }

    public static RouteDao getInstance() {
        return INSTANCE;
    }
}
