package com.academy.airport.dao.impl;

import com.academy.airport.dao.Dao;
import com.academy.airport.entity.Seat;
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
public class SeatDao implements Dao<Integer, Seat> {
    private static final SeatDao INSTANCE = new SeatDao();
    @Language("PostgreSQL")
    private static final String DELETE_SQL = """
            DELETE
            FROM airport_storage.seat
            WHERE airplane_id = ?;""";
    @Language("PostgreSQL")
    private static final String SAVE_SQL = """
            INSERT INTO airport_storage.seat(airplane_id, seat_no)
            VALUES (?, ?);""";
    @Language("PostgreSQL")
    private static final String INIT_SQL = """
            INSERT INTO airport_storage.seat (airplane_id, seat_no)
            SELECT id,
                   s.column1
            FROM airport_storage.airplane
                     CROSS JOIN (VALUES ('A1'), ('A2'), ('B1'), ('B2'), ('C1'), ('C2'), ('D1'), ('D2') ORDER BY 1) s
            WHERE aircompany_id = ?;""";
    @Language("PostgreSQL")
    private static final String COUNT_SQL = """
            SELECT count(seat_no)
            FROM airport_storage.seat
            WHERE airplane_id = ?;""";
    @Language("PostgreSQL")
    private static final String UPDATE_SQL = """
            UPDATE airport_storage.seat
            SET seat_no = ?
            WHERE airplane_id = ?;""";
    @Language("PostgreSQL")
    private static final String FIND_ALL_SQL = """
            SELECT airplane_id,
                   seat_no
            FROM airport_storage.seat""";
    @Language("PostgreSQL")
    private static final String FIND_BY_ID_SQL = FIND_ALL_SQL + " WHERE airplane_id = ?;";

    @Override
    @SneakyThrows
    public Seat save(final @NotNull Seat entity) {
        try (var connection = ConnectionManager.get();
             var savePrepareStatement = connection.prepareStatement(SAVE_SQL, RETURN_GENERATED_KEYS);
             var countPrepareStatement = connection.prepareStatement(COUNT_SQL);
             var initPrepareStatement = connection.prepareStatement(INIT_SQL)) {
            String columLabel = "seat_no";
            countPrepareStatement.setObject(1, entity.getAirplaneId());
            var resultSet = countPrepareStatement.executeQuery();
            if (resultSet.next() || resultSet.getInt(columLabel) == 0) {
                initPrepareStatement.setObject(1, entity.getAirplaneId());
                initPrepareStatement.executeUpdate();
            }
            savePrepareStatement.setObject(1, entity.getAirplaneId());
            savePrepareStatement.setObject(2, entity.getSeatNo());
            savePrepareStatement.executeUpdate();
            var generatedKeys = savePrepareStatement.getGeneratedKeys();
            if (generatedKeys.next()) {
                entity.setSeatNo(generatedKeys.getObject(columLabel, String.class));
            }
            return entity;
        }
    }

    @Override
    @SneakyThrows
    public void update(final @NotNull Seat entity) {
        try (var connection = ConnectionManager.get();
             var prepareStatement = connection.prepareStatement(UPDATE_SQL)) {
            prepareStatement.setObject(1, entity.getAirplaneId());
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
    public Optional<Seat> findById(Integer id, Connection connection) {
        try (var prepareStatement = connection.prepareStatement(FIND_BY_ID_SQL)) {
            prepareStatement.setObject(1, id);
            var resultSet = prepareStatement.executeQuery();
            Seat seat = null;
            if (resultSet.next()) {
                seat = buildSeat(resultSet);
            }
            return Optional.ofNullable(seat);
        }
    }

    @Override
    @SneakyThrows
    public Optional<Seat> findById(final Integer id) {
        try (var connection = ConnectionManager.get()) {
            return findById(id, connection);
        }
    }

    @Override
    @SneakyThrows
    public List<Seat> findAll() {
        try (var connection = ConnectionManager.get();
             var prepareStatement = connection.prepareStatement(FIND_ALL_SQL)) {
            var resultSet = prepareStatement.executeQuery();
            List<Seat> seatList = new ArrayList<>();
            while (resultSet.next()) {
                seatList.add(buildSeat(resultSet));
            }
            return seatList;
        }
    }

    public static SeatDao getInstance() {
        return INSTANCE;
    }

    @SneakyThrows
    private Seat buildSeat(@NotNull ResultSet resultSet) {
        return Seat.builder()
                .airplaneId(resultSet.getObject("airplane_id", Integer.class))
                .seatNo(resultSet.getObject("seat_no", String.class))
                .build();
    }
}
