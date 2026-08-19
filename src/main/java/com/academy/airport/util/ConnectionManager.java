package com.academy.airport.util;

import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.sql.DriverManager;
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
    private static final int DEFAULT_POOL_SIZE = 5;

    private static BlockingQueue<Connection> pool;
    private static List<Connection> sourceConnections;

    static {
        initConnectionPool();
    }

    @SneakyThrows
    public static @NotNull Connection get() {
        return pool.take();
    }

    @SneakyThrows
    public static void close() {
        for (Connection connection : sourceConnections) {
            if (!connection.isClosed()) {
                connection.close();
            }
        }
        pool.clear();
    }

    private static void initConnectionPool() {
        var poolSize = PropertiesUtil.get(POOL_SIZE_KEY);
        var size = poolSize == null || poolSize.isBlank()
                ? DEFAULT_POOL_SIZE
                : Integer.parseInt(poolSize);
        if (size <= 0) {
            throw new IllegalArgumentException("db.pool.size must be greater than zero");
        }

        pool = new ArrayBlockingQueue<>(size);
        sourceConnections = new ArrayList<>(size);

        for (int i = 0; i < size; i++) {
            var sourceConnection = open();
            Connection proxyConnection = (Connection) Proxy.newProxyInstance(
                    ConnectionManager.class.getClassLoader(),
                    new Class[]{Connection.class},
                    (proxy, method, args) -> {
                        if ("close".equals(method.getName())) {
                            pool.offer((Connection) proxy);
                            return null;
                        }
                        try {
                            return method.invoke(sourceConnection, args);
                        } catch (InvocationTargetException exception) {
                            throw exception.getCause();
                        }
                    });
            pool.add(proxyConnection);
            sourceConnections.add(sourceConnection);
        }
    }

    @SneakyThrows
    private static Connection open() {
        return DriverManager.getConnection(
                PropertiesUtil.require(URL_KEY),
                PropertiesUtil.require(USERNAME_KEY),
                PropertiesUtil.require(PASSWORD_KEY));
    }
}
