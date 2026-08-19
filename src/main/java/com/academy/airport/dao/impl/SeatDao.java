package com.academy.airport.dao.impl;

import com.academy.airport.dao.Dao;
import com.academy.airport.entity.Seat;
import com.academy.airport.entity.SeatPk;
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

import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
public class SeatDao implements Dao<SeatPk, Seat> {
    private static final SeatDao INSTANCE = new SeatDao();

    @Language("PostgreSQL")
    private static final String DELETE_SQL = "DELETE FROM airport_storage.seat WHERE airplane_id = ? AND seat_no = ?;";
    @Language("PostgreSQL")
    private static final String SAVE_SQL = "INSERT INTO airport_storage.seat(airplane_id, seat_no) VALUES (?, ?);";
    @Language("PostgreSQL")
    private static final String FIND_ALL_SQL = "SELECT airplane_id, seat_no FROM airport_storage.seat";
    @Language("PostgreSQL")
    private static final String FIND_BY_ID_SQL = FIND_ALL_SQL + " WHERE airplane_id = ? AND seat_no = ?;";
    @Language("PostgreSQL")
    private static final String FIND_BY_AIRPLANE_SQL = FIND_ALL_SQL + " WHERE airplane_id = ? ORDER BY seat_no;";

    @Override
    @SneakyThrows
    public Seat save(final @NotNull Seat entity) {
        try (var connection = ConnectionManager.get();
             var statement = connection.prepareStatement(SAVE_SQL)) {
            statement.setObject(1, entity.getAirplaneId());
            statement.setString(2, entity.getSeatNo());
            statement.executeUpdate();
            return entity;
        }
    }

    @Override
    public void update(final @NotNull Seat entity) {
        throw new UnsupportedOperationException(
                "Seat has no mutable columns; delete the old composite key and save a new seat instead");
    }

    @Override
    @SneakyThrows
    public boolean delete(final SeatPk id) {
        try (var connection = ConnectionManager.get();
             var statement = connection.prepareStatement(DELETE_SQL)) {
            bindKey(statement, id);
            return statement.executeUpdate() > 0;
        }
    }

    @Override
    @SneakyThrows
    public Optional<Seat> findById(final SeatPk id) {
        try (var connection = ConnectionManager.get()) {
            return findById(id, connection);
        }
    }

    @Override
    @SneakyThrows
    public Optional<Seat> findById(final SeatPk id, final Connection connection) {
        try (var statement = connection.prepareStatement(FIND_BY_ID_SQL)) {
            bindKey(statement, id);
            try (var resultSet = statement.executeQuery()) {
                return resultSet.next() ? Optional.of(buildSeat(resultSet)) : Optional.empty();
            }
        }
    }

    @Override
    @SneakyThrows
    public List<Seat> findAll() {
        try (var connection = ConnectionManager.get();
             var statement = connection.prepareStatement(FIND_ALL_SQL);
             var resultSet = statement.executeQuery()) {
            return readSeats(resultSet);
        }
    }

    @SneakyThrows
    public List<Seat> findAllByAirplaneId(final Integer airplaneId) {
        try (var connection = ConnectionManager.get();
             var statement = connection.prepareStatement(FIND_BY_AIRPLANE_SQL)) {
            statement.setObject(1, airplaneId);
            try (var resultSet = statement.executeQuery()) {
                return readSeats(resultSet);
            }
        }
    }

    @SneakyThrows
    private void bindKey(final java.sql.PreparedStatement statement, final SeatPk id) {
        if (id == null || id.getAirplaneId() == null || id.getSeatNo() == null) {
            throw new IllegalArgumentException("Seat key must contain airplaneId and seatNo");
        }
        statement.setObject(1, id.getAirplaneId());
        statement.setString(2, id.getSeatNo());
    }

    @SneakyThrows
    private List<Seat> readSeats(final ResultSet resultSet) {
        List<Seat> seats = new ArrayList<>();
        while (resultSet.next()) {
            seats.add(buildSeat(resultSet));
        }
        return seats;
    }

    @SneakyThrows
    private Seat buildSeat(final ResultSet resultSet) {
        return Seat.builder()
                .airplaneId(resultSet.getObject("airplane_id", Integer.class))
                .seatNo(resultSet.getString("seat_no"))
                .build();
    }

    public static SeatDao getInstance() {
        return INSTANCE;
    }
}
