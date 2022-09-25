package com.academy.airport.util;

import com.academy.airport.exception.ConnectionException;
import lombok.experimental.UtilityClass;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

@UtilityClass
public class ConnectionManager {
    private static final String URL_KEY = "db.url";
    private static final String USERNAME_KEY = "db.username";
    private static final String PASSWORD_KEY = "db.password";
    private static final String POOL_SIZE_KEY = "db.pool.size";
    private static final Integer DEFAULT_POOL_SIZE = 10;
    private static BlockingQueue<Connection> pool;
    private static List<Connection> sourceConnection;

    static {
        initConnectionPoll();
    }

    public static @NotNull Connection get() {
        try {
            return pool.take();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new ConnectionException(e);
        }
    }

    public static void close() {
        try {
            for (Connection connection : sourceConnection) {
                connection.close();
            }
        } catch (SQLException e) {
            throw new ConnectionException(e);
        }
    }

    private static void initConnectionPoll() {
        var poolSize = PropertiesUtil.get(POOL_SIZE_KEY);
        var size = poolSize == null ? DEFAULT_POOL_SIZE : Integer.parseInt(poolSize);

        pool = new ArrayBlockingQueue<>(size);
        sourceConnection = new ArrayList<>(size);

        for (int i = 0; i < size; i++) {
            var connection = open();
            var proxyConnection = (Connection)
                    Proxy.newProxyInstance(ConnectionManager.class.getClassLoader(),
                            new Class[]{Connection.class},
                            (proxy, method, args) -> "close".equals(method.getName())
                                    ? pool.add(connection)
                                    : method.invoke(connection, args));
            pool.add(proxyConnection);
            sourceConnection.add(connection);
        }
    }

    private static Connection open() {
        try {
            return DriverManager.getConnection(
                    PropertiesUtil.get(URL_KEY),
                    PropertiesUtil.get(USERNAME_KEY),
                    PropertiesUtil.get(PASSWORD_KEY));
        } catch (SQLException e) {
            throw new ConnectionException(e);
        }
    }
}
