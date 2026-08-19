package com.academy.airport.util;

import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;

import java.io.InputStream;
import java.util.Properties;

@UtilityClass
public class PropertiesUtil {
    private static final String PROPERTIES_FILE = "application.properties";
    private static final Properties PROPERTIES = new Properties();

    static {
        loadProperties();
    }

    public static String get(final String key) {
        var environmentKey = key.toUpperCase().replace('.', '_');
        var environmentValue = System.getenv(environmentKey);
        if (environmentValue != null && !environmentValue.isBlank()) {
            return environmentValue;
        }
        return PROPERTIES.getProperty(key);
    }

    public static String require(final String key) {
        var value = get(key);
        if (value == null || value.isBlank()) {
            throw new IllegalStateException("Missing required configuration: " + key);
        }
        return value;
    }

    @SneakyThrows
    private static void loadProperties() {
        try (InputStream inputStream = PropertiesUtil.class.getClassLoader()
                .getResourceAsStream(PROPERTIES_FILE)) {
            if (inputStream == null) {
                throw new IllegalStateException("Cannot find " + PROPERTIES_FILE + " on the classpath");
            }
            PROPERTIES.load(inputStream);
        }
    }
}
