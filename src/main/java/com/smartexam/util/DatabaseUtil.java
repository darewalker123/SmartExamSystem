package com.smartexam.util;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public final class DatabaseUtil {
    private static final Properties FILE_PROPERTIES = loadFileProperties();

    private DatabaseUtil() {
    }

    public static Connection getConnection() throws SQLException {
        String host = value("DB_HOST", "localhost");
        String port = value("DB_PORT", "3306");
        String name = value("DB_NAME", "smart_exam_system");
        String user = value("DB_USER", "root");
        String password = value("DB_PASSWORD", "");
        String url = "jdbc:mysql://" + host + ":" + port + "/" + name
                + "?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new SQLException("MySQL JDBC driver is missing. Add mysql-connector-j to WEB-INF/lib or use Maven.", e);
        }
        return DriverManager.getConnection(url, user, password);
    }

    private static String value(String key, String defaultValue) {
        String env = System.getenv(key);
        if (env != null && !env.isBlank()) {
            return env;
        }
        return FILE_PROPERTIES.getProperty(key, defaultValue);
    }

    private static Properties loadFileProperties() {
        Properties properties = new Properties();
        try (InputStream input = DatabaseUtil.class.getClassLoader().getResourceAsStream("db.properties")) {
            if (input != null) {
                properties.load(input);
            }
        } catch (IOException ignored) {
            // Environment variables still provide a complete configuration path.
        }
        return properties;
    }
}
