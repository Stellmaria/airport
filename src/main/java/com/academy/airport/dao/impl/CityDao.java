package com.academy.airport.dao.impl;

import com.academy.airport.dao.Dao;
import com.academy.airport.entity.route.City;
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
public class CityDao implements Dao<Integer, City> {
    private static final CityDao INSTANCE = new CityDao();
    @Language("PostgreSQL")
    private static final String SAVE_SQL = "INSERT INTO airport_storage.city(country_id, name) "
            + "VALUES (?, ?);";

    @Override
    public City save(final @NotNull City entity) {
        try (var connection = ConnectionManager.get();
             var prepareStatement = connection.prepareStatement(SAVE_SQL, RETURN_GENERATED_KEYS)) {
            prepareStatement.setObject(1, entity.getCountry().getId());
            prepareStatement.setObject(2, entity.getName());

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
    public void update(final City entity) {

    }

    @Override
    public boolean delete(final Integer id) {
        try (var connection = ConnectionManager.get();
             var prepareStatement = connection.prepareStatement(SqlHelper.getDeleteSql("city"))) {
            prepareStatement.setInt(1, id);
            return prepareStatement.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DaoException(e);
        }
    }

    @Override
    public Optional<City> findById(final Integer id) {
        return Optional.empty();
    }

    @Override
    public List<City> findAll() {
        return null;
    }

    public static CityDao getInstance() {
        return INSTANCE;
    }
}
