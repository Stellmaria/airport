package com.academy.airport.dao.impl;

import com.academy.airport.dao.Dao;
import com.academy.airport.entity.Ticket;
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
public class TicketDao implements Dao<Long, Ticket> {
    private static final TicketDao INSTANCE = new TicketDao();

    @Language("PostgreSQL")
    private static final String DELETE_SQL = "DELETE FROM airport_storage.ticket WHERE id = ?;";
    @Language("PostgreSQL")
    private static final String SAVE_SQL = "INSERT INTO airport_storage.ticket(user_id, route_id, seat_no, cost) VALUES (?, ?, ?, ?);";
    @Language("PostgreSQL")
    private static final String UPDATE_SQL = "UPDATE airport_storage.ticket SET user_id = ?, route_id = ?, seat_no = ?, cost = ? WHERE id = ?;";
    @Language("PostgreSQL")
    private static final String FIND_ALL_SQL = "SELECT id, user_id, route_id, seat_no, cost FROM airport_storage.ticket";
    @Language("PostgreSQL")
    private static final String FIND_BY_ID_SQL = FIND_ALL_SQL + " WHERE id = ?;";

    @Override
    @SneakyThrows
    public Ticket save(final @NotNull Ticket entity) {
        try (var connection = ConnectionManager.get();
             var statement = connection.prepareStatement(SAVE_SQL, RETURN_GENERATED_KEYS)) {
            bindTicket(statement, entity);
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
    public void update(final @NotNull Ticket entity) {
        try (var connection = ConnectionManager.get();
             var statement = connection.prepareStatement(UPDATE_SQL)) {
            bindTicket(statement, entity);
            statement.setObject(5, entity.getId());
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
    public Optional<Ticket> findById(final Long id) {
        try (var connection = ConnectionManager.get()) {
            return findById(id, connection);
        }
    }

    @Override
    @SneakyThrows
    public Optional<Ticket> findById(final Long id, final Connection connection) {
        try (var statement = connection.prepareStatement(FIND_BY_ID_SQL)) {
            statement.setObject(1, id);
            try (var resultSet = statement.executeQuery()) {
                return resultSet.next() ? Optional.of(buildTicket(resultSet)) : Optional.empty();
            }
        }
    }

    @Override
    @SneakyThrows
    public List<Ticket> findAll() {
        try (var connection = ConnectionManager.get();
             var statement = connection.prepareStatement(FIND_ALL_SQL);
             var resultSet = statement.executeQuery()) {
            List<Ticket> tickets = new ArrayList<>();
            while (resultSet.next()) {
                tickets.add(buildTicket(resultSet));
            }
            return tickets;
        }
    }

    @SneakyThrows
    private void bindTicket(final java.sql.PreparedStatement statement, final Ticket entity) {
        statement.setObject(1, entity.getUserId());
        statement.setObject(2, entity.getRouteId());
        statement.setString(3, entity.getSeatNo());
        statement.setBigDecimal(4, entity.getCost());
    }

    @SneakyThrows
    private Ticket buildTicket(final ResultSet resultSet) {
        return Ticket.builder()
                .id(resultSet.getObject("id", Long.class))
                .userId(resultSet.getObject("user_id", Integer.class))
                .routeId(resultSet.getObject("route_id", Long.class))
                .seatNo(resultSet.getString("seat_no"))
                .cost(resultSet.getBigDecimal("cost"))
                .build();
    }

    public static TicketDao getInstance() {
        return INSTANCE;
    }
}
