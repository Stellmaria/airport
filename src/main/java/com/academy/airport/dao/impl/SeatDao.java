package com.academy.airport.dao.impl;

import com.academy.airport.dao.Dao;
import com.academy.airport.entity.airport.Seat;
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
public class SeatDao implements Dao<Integer, Seat> {
    private static final SeatDao INSTANCE = new SeatDao();
    @Language("PostgreSQL")
    private static final String DELETE_SQL = "DELETE "
            + "FROM airport_storage.seat "
            + "WHERE airplane_id = ?;";
    @Language("PostgreSQL")
    private static final String SAVE_SQL = "INSERT INTO airport_storage.seat(airplane_id, seat_no)"
            + "VALUES (?, ?);";
    @Language("PostgreSQL")
    private static final String INIT_SQL = "INSERT INTO airport_storage.seat (airplane_id, seat_no) "
            + "SELECT id, s.column1 "
            + "FROM airport_storage.airplane "
            + "CROSS JOIN (VALUES ('A1'), ('A2'), ('B1'), ('B2'), ('C1'), ('C2'), ('D1'), ('D2') ORDER BY 1) s " +
            "WHERE aircompany_id = ?;";
    @Language("PostgreSQL")
    private static final String COUNT_SQL = "SELECT count(seat_no) "
            + "FROM airport_storage.seat "
            + "WHERE airplane_id = ?;";

    @Override
    public Seat save(final @NotNull Seat entity) {
        try (var connection = ConnectionManager.get();
             var savePrepareStatement = connection.prepareStatement(SAVE_SQL, RETURN_GENERATED_KEYS);
             var countPrepareStatement = connection.prepareStatement(COUNT_SQL);
             var initPrepareStatement = connection.prepareStatement(INIT_SQL)) {
            String columLabel = "seat_no";

            countPrepareStatement.setObject(1, entity.getAirplane().getId());
            var resultSet = countPrepareStatement.executeQuery();
            if (resultSet.next() || resultSet.getInt(columLabel) == 0) {
                initPrepareStatement.setObject(1, entity.getAirplane().getId());
                initPrepareStatement.executeUpdate();
            }

            savePrepareStatement.setObject(1, entity.getAirplane().getId());
            savePrepareStatement.setObject(2, entity.getSeatNo());
            savePrepareStatement.executeUpdate();

            var generatedKeys = savePrepareStatement.getGeneratedKeys();
            if (generatedKeys.next()) {
                entity.setSeatNo(generatedKeys.getString(columLabel));
            }
            return entity;
        } catch (SQLException e) {
            throw new DaoException(e);
        }
    }

    @Override
    public void update(final Seat entity) {

    }

    @Override
    public boolean delete(final Integer id) {
        try (var connection = ConnectionManager.get();
             var prepareStatement = connection.prepareStatement(DELETE_SQL)) {
            prepareStatement.setInt(1, id);
            return prepareStatement.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DaoException(e);
        }
    }

    @Override
    public Optional<Seat> findById(final Integer id) {
        return Optional.empty();
    }

    @Override
    public List<Seat> findAll() {
        return null;
    }

    public static SeatDao getInstance() {
        return INSTANCE;
    }
}
