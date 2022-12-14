package com.academy.airport.dao.impl;

import com.academy.airport.dao.Dao;
import com.academy.airport.entity.User;
import com.academy.airport.util.ConnectionManager;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
import org.intellij.lang.annotations.Language;
import org.jetbrains.annotations.NotNull;

import java.sql.Connection;
import java.util.List;
import java.util.Optional;

import static java.sql.Statement.RETURN_GENERATED_KEYS;
import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
public class UserDao implements Dao<Integer, User> {
    private static final UserDao INSTANCE = new UserDao();
    @Language("PostgreSQL")
    private static final String DELETE_SQL = "DELETE "
            + "FROM airport_storage.users "
            + "WHERE id = ?;";
    @Language("PostgreSQL")
    private static final String SAVE_SQL = "INSERT INTO airport_storage.users(first_name, last_name, passport_no,"
            + " birthday, email, role, gender)"
            + "VALUES (?, ?, ?, ?, ?, ?, ?);";
    @Language("PostgreSQL")
    private static final String UPDATE_SQL = "UPDATE airport_storage.users\n"
            + "SET first_name = ?, last_name = ?, passport_no = ?, birthday = ?, email = ?, role = ?, gender = ?"
            + "WHERE id = ?;";
    @Language("PostgreSQL")
    private static final String FIND_ALL_SQL = "SELECT id, first_name, last_name, passport_no,"
            + " birthday, email, role, gender "
            + "FROM airport_storage.users ";
    @Language("PostgreSQL")
    private static final String FIND_BY_ID_SQL = FIND_ALL_SQL + "WHERE id = ?;";

    @Override
    @SneakyThrows
    public User save(final @NotNull User entity) {
        try (var connection = ConnectionManager.get();
             var prepareStatement = connection.prepareStatement(SAVE_SQL, RETURN_GENERATED_KEYS)) {
            prepareStatement.setObject(1, entity.getFirstName());
            prepareStatement.setObject(2, entity.getLastName());
            prepareStatement.setObject(3, entity.getPassportNo());
            prepareStatement.setObject(4, entity.getBirthday());
            prepareStatement.setObject(5, entity.getEmail());
            prepareStatement.setObject(6, entity.getRole());
            prepareStatement.setObject(7, entity.getGender());

            prepareStatement.executeUpdate();

            var generatedKeys = prepareStatement.getGeneratedKeys();
            if (generatedKeys.next()) {
                entity.setId(generatedKeys.getObject("id", Integer.class));
            }
            return entity;
        }
    }

    @Override
    @SneakyThrows
    public void update(final @NotNull User entity) {
        try (var connection = ConnectionManager.get();
             var prepareStatement = connection.prepareStatement(UPDATE_SQL)) {
            prepareStatement.setObject(1, entity.getFirstName());
            prepareStatement.setObject(2, entity.getLastName());
            prepareStatement.setObject(3, entity.getPassportNo());
            prepareStatement.setObject(4, entity.getBirthday());
            prepareStatement.setObject(5, entity.getEmail());
            prepareStatement.setObject(6, entity.getRole());
            prepareStatement.setObject(7, entity.getGender());
            prepareStatement.setObject(8, entity.getId());

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
    public Optional<User> findById(final Integer id) {
        return Optional.empty();
    }

    @Override
    public Optional<User> findById(Integer id, Connection connection) {
        return Optional.empty();
    }

    @Override
    @SneakyThrows
    public List<User> findAll() {
        return null;
    }

    public static UserDao getInstance() {
        return INSTANCE;
    }
}
