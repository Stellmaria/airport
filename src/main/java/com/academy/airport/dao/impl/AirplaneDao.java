package com.academy.airport.dao.impl;

import com.academy.airport.dao.Dao;
import com.academy.airport.entity.airport.Airplane;
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
public class AirplaneDao implements Dao<Integer, Airplane> {
    private static final AirplaneDao INSTANCE = new AirplaneDao();
    @Language("PostgreSQL")
    private static final String SAVE_SQL = "INSERT INTO airport_storage.airplane(model, aircompany_id) "
            + "VALUES (?, ?);";

    @Override
    public Airplane save(final @NotNull Airplane entity) {
        try (var connection = ConnectionManager.get();
             var prepareStatement = connection.prepareStatement(SAVE_SQL, RETURN_GENERATED_KEYS)) {
            prepareStatement.setObject(1, entity.getModel());
            prepareStatement.setObject(2, entity.getAircompany().getId());

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
    public void update(final Airplane entity) {

    }

    @Override
    public boolean delete(final Integer id) {
        try (var connection = ConnectionManager.get();
             var prepareStatement = connection.prepareStatement(
                     SqlHelper.getDeleteSql("airplane"))) {
            prepareStatement.setInt(1, id);
            return prepareStatement.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DaoException(e);
        }
    }

    @Override
    public Optional<Airplane> findById(final Integer id) {
        return Optional.empty();
    }

    @Override
    public List<Airplane> findAll() {
        return null;
    }

    public static AirplaneDao getInstance() {
        return INSTANCE;
    }
}
