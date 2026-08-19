package com.academy.airport.dao.impl;

import com.academy.airport.dao.Dao;
import com.academy.airport.entity.User;
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
public class UserDao implements Dao<Integer, User> {
    private static final UserDao INSTANCE = new UserDao();

    @Language("PostgreSQL")
    private static final String DELETE_SQL = "DELETE FROM airport_storage.users WHERE id = ?;";
    @Language("PostgreSQL")
    private static final String SAVE_SQL = """
            INSERT INTO airport_storage.users(
                first_name, last_name, passport_no, birthday, email, role, gender
            ) VALUES (?, ?, ?, ?, ?, ?, ?);
            """;
    @Language("PostgreSQL")
    private static final String UPDATE_SQL = """
            UPDATE airport_storage.users
            SET first_name = ?, last_name = ?, passport_no = ?, birthday = ?,
                email = ?, role = ?, gender = ?
            WHERE id = ?;
            """;
    @Language("PostgreSQL")
    private static final String FIND_ALL_SQL = """
            SELECT id, first_name, last_name, passport_no, birthday, email, role, gender
            FROM airport_storage.users
            """;
    @Language("PostgreSQL")
    private static final String FIND_BY_ID_SQL = FIND_ALL_SQL + " WHERE id = ?;";

    @Override
    @SneakyThrows
    public User save(final @NotNull User entity) {
        try (var connection = ConnectionManager.get();
             var statement = connection.prepareStatement(SAVE_SQL, RETURN_GENERATED_KEYS)) {
            bindUser(statement, entity);
            statement.executeUpdate();
            try (var generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    entity.setId(generatedKeys.getObject("id", Integer.class));
                }
            }
            return entity;
        }
    }

    @Override
    @SneakyThrows
    public void update(final @NotNull User entity) {
        try (var connection = ConnectionManager.get();
             var statement = connection.prepareStatement(UPDATE_SQL)) {
            bindUser(statement, entity);
            statement.setObject(8, entity.getId());
            statement.executeUpdate();
        }
    }

    @Override
    @SneakyThrows
    public boolean delete(final Integer id) {
        try (var connection = ConnectionManager.get();
             var statement = connection.prepareStatement(DELETE_SQL)) {
            statement.setObject(1, id);
            return statement.executeUpdate() > 0;
        }
    }

    @Override
    @SneakyThrows
    public Optional<User> findById(final Integer id) {
        try (var connection = ConnectionManager.get()) {
            return findById(id, connection);
        }
    }

    @Override
    @SneakyThrows
    public Optional<User> findById(final Integer id, final Connection connection) {
        try (var statement = connection.prepareStatement(FIND_BY_ID_SQL)) {
            statement.setObject(1, id);
            try (var resultSet = statement.executeQuery()) {
                return resultSet.next() ? Optional.of(buildUser(resultSet)) : Optional.empty();
            }
        }
    }

    @Override
    @SneakyThrows
    public List<User> findAll() {
        try (var connection = ConnectionManager.get();
             var statement = connection.prepareStatement(FIND_ALL_SQL);
             var resultSet = statement.executeQuery()) {
            List<User> users = new ArrayList<>();
            while (resultSet.next()) {
                users.add(buildUser(resultSet));
            }
            return users;
        }
    }

    @SneakyThrows
    private void bindUser(final java.sql.PreparedStatement statement, final User entity) {
        statement.setString(1, entity.getFirstName());
        statement.setString(2, entity.getLastName());
        statement.setString(3, entity.getPassportNo());
        statement.setDate(4, entity.getBirthday());
        statement.setString(5, entity.getEmail());
        statement.setString(6, entity.getRole());
        statement.setString(7, entity.getGender());
    }

    @SneakyThrows
    private User buildUser(final ResultSet resultSet) {
        return User.builder()
                .id(resultSet.getObject("id", Integer.class))
                .firstName(resultSet.getString("first_name"))
                .lastName(resultSet.getString("last_name"))
                .passportNo(resultSet.getString("passport_no"))
                .birthday(resultSet.getDate("birthday"))
                .email(resultSet.getString("email"))
                .role(resultSet.getString("role"))
                .gender(resultSet.getString("gender"))
                .build();
    }

    public static UserDao getInstance() {
        return INSTANCE;
    }
}
