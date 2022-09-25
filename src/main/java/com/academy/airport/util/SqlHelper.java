package com.academy.airport.util;

import lombok.experimental.UtilityClass;
import org.intellij.lang.annotations.Language;

@UtilityClass
public class SqlHelper {
    @Language("PostgreSQL")
    private static final String DELETE_SQL = "DELETE "
            + "FROM airport_storage.%s "
            + "WHERE id = ?;";

    public static String getDeleteSql(String name) {
        return String.format(DELETE_SQL, name);
    }
}
