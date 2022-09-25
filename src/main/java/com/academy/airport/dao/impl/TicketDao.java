package com.academy.airport.dao.impl;

import com.academy.airport.dao.Dao;
import com.academy.airport.entity.ticket.Ticket;
import com.academy.airport.exception.DaoException;
import com.academy.airport.util.ConnectionManager;
import lombok.NoArgsConstructor;
import org.intellij.lang.annotations.Language;
import org.jetbrains.annotations.NotNull;

import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;

import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
public class TicketDao implements Dao<Long, Ticket> {
    private static final TicketDao INSTANCE = new TicketDao();
    @Language("PostgreSQL")
    private static final String DELETE_SQL = "DELETE FROM airport_storage.ticket"
            + " WHERE id = ?;";
    private static final String SAVE_SQL = "INSERT INTO airport_storage.ticket(user_id, route_id, seat_no, cost)"
            + " VALUES (?, ?, ?, ?);";

    @Override
    public Ticket save(final @NotNull Ticket entity) {
        try (var connection = ConnectionManager.get();
             var prepareStatement = connection.prepareStatement(SAVE_SQL, Statement.RETURN_GENERATED_KEYS)) {
            prepareStatement.setObject(1, entity.getUser().getId());
            prepareStatement.setObject(2, entity.getRoute().getId());
            prepareStatement.setObject(3, entity.getSeatNo());
            prepareStatement.setObject(4, entity.getCost());

            prepareStatement.executeUpdate();

            var generatedKeys = prepareStatement.getGeneratedKeys();
            if (generatedKeys.next()) {
                entity.setId(generatedKeys.getInt("id"));
            }
            return entity;
        } catch (SQLException e) {
            throw new DaoException(e);
        }
    }

    @Override
    public void update(final Ticket entity) {

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
    public Optional<Ticket> findById(final Long id) {
        return Optional.empty();
    }

    @Override
    public List<Ticket> findAll() {
        return null;
    }

    public static TicketDao getInstance() {
        return INSTANCE;
    }
}
