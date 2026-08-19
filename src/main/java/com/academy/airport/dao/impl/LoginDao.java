package com.academy.airport.dao.impl;

import com.academy.airport.dao.Dao;
import com.academy.airport.entity.Login;
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
public class LoginDao implements Dao<Integer, Login> {
    private static final LoginDao INSTANCE = new LoginDao();

    @Language("PostgreSQL")
    private static final String DELETE_SQL = "DELETE FROM airport_storage.login WHERE id = ?;";
    @Language("PostgreSQL")
    private static final String SAVE_SQL = "INSERT INTO airport_storage.login(user_id, login, password) VALUES (?, ?, ?);";
    @Language("PostgreSQL")
    private static final String UPDATE_SQL = "UPDATE airport_storage.login SET user_id = ?, login = ?, password = ? WHERE id = ?;";
    @Language("PostgreSQL")
    private static final String FIND_ALL_SQL = "SELECT id, user_id, login, password FROM airport_storage.login";
    @Language("PostgreSQL")
    private static final String FIND_BY_ID_SQL = FIND_ALL_SQL + " WHERE id = ?;";
    @Language("PostgreSQL")
    private static final String FIND_BY_LOGIN_SQL = FIND_ALL_SQL + " WHERE login = ?;";

    @Override
    @SneakyThrows
    public Login save(final @NotNull Login entity) {
        try (var connection = ConnectionManager.get();
             var statement = connection.prepareStatement(SAVE_SQL, RETURN_GENERATED_KEYS)) {
            bindLogin(statement, entity);
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
    public void update(final @NotNull Login entity) {
        try (var connection = ConnectionManager.get();
             var statement = connection.prepareStatement(UPDATE_SQL)) {
            bindLogin(statement, entity);
            statement.setObject(4, entity.getId());
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
    public Optional<Login> findById(final Integer id) {
        try (var connection = ConnectionManager.get()) {
            return findById(id, connection);
        }
    }

    @Override
    @SneakyThrows
    public Optional<Login> findById(final Integer id, final Connection connection) {
        try (var statement = connection.prepareStatement(FIND_BY_ID_SQL)) {
            statement.setObject(1, id);
            try (var resultSet = statement.executeQuery()) {
                return resultSet.next() ? Optional.of(buildLogin(resultSet)) : Optional.empty();
            }
        }
    }

    @SneakyThrows
    public Optional<Login> findByLogin(final String login) {
        try (var connection = ConnectionManager.get();
             var statement = connection.prepareStatement(FIND_BY_LOGIN_SQL)) {
            statement.setString(1, login);
            try (var resultSet = statement.executeQuery()) {
                return resultSet.next() ? Optional.of(buildLogin(resultSet)) : Optional.empty();
            }
        }
    }

    @Override
    @SneakyThrows
    public List<Login> findAll() {
        try (var connection = ConnectionManager.get();
             var statement = connection.prepareStatement(FIND_ALL_SQL);
             var resultSet = statement.executeQuery()) {
            List<Login> logins = new ArrayList<>();
            while (resultSet.next()) {
                logins.add(buildLogin(resultSet));
            }
            return logins;
        }
    }

    @SneakyThrows
    private void bindLogin(final java.sql.PreparedStatement statement, final Login entity) {
        statement.setObject(1, entity.getUserId());
        statement.setString(2, entity.getLogin());
        statement.setString(3, entity.getPassword());
    }

    @SneakyThrows
    private Login buildLogin(final ResultSet resultSet) {
        return Login.builder()
                .id(resultSet.getObject("id", Integer.class))
                .userId(resultSet.getObject("user_id", Integer.class))
                .login(resultSet.getString("login"))
                .password(resultSet.getString("password"))
                .build();
    }

    public static LoginDao getInstance() {
        return INSTANCE;
    }
}
