package com.academy.airport.dao.impl;

import com.academy.airport.dao.Dao;
import com.academy.airport.entity.Ticket;
import com.academy.airport.util.ConnectionManager;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
import org.intellij.lang.annotations.Language;
import org.jetbrains.annotations.NotNull;

import java.sql.Connection;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;

import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
public class TicketDao implements Dao<Long, Ticket> {
    private static final TicketDao INSTANCE = new TicketDao();
    @Language("PostgreSQL")
    private static final String DELETE_SQL = "DELETE "
            + "FROM airport_storage.ticket "
            + "WHERE id = ?;";
    private static final String SAVE_SQL = "INSERT INTO airport_storage.ticket(user_id, route_id, seat_no, cost)"
            + " VALUES (?, ?, ?, ?);";

    @Override
    @SneakyThrows
    public Ticket save(final @NotNull Ticket entity) {
        try (var connection = ConnectionManager.get();
             var prepareStatement = connection.prepareStatement(SAVE_SQL, Statement.RETURN_GENERATED_KEYS)) {
            prepareStatement.setObject(1, entity.getUserId());
            prepareStatement.setObject(2, entity.getRouteId());
            prepareStatement.setObject(3, entity.getSeatNo());
            prepareStatement.setObject(4, entity.getCost());

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
    public void update(final Ticket entity) {

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
    public Optional<Ticket> findById(final Long id) {
        return Optional.empty();
    }

    @Override
    public Optional<Ticket> findById(Long id, Connection connection) {
        return Optional.empty();
    }

    @Override
    @SneakyThrows
    public List<Ticket> findAll() {
        return null;
    }

    public static TicketDao getInstance() {
        return INSTANCE;
    }
}
