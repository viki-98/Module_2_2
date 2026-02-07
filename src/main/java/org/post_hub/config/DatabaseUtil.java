package org.post_hub.config;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public final class DatabaseUtil {
    private static final String PROPERTIES_FILE = "application.properties";

    private static volatile DatabaseUtil instance;

    private final String url;
    private final String user;
    private final String password;
    private final boolean autoCommit;

    private DatabaseUtil() {
        Properties props = loadProperties(PROPERTIES_FILE);
        this.url = requireProperty(props, "db.url");
        this.user = requireProperty(props, "db.user");
        this.password = requireProperty(props, "db.password");
        this.autoCommit = Boolean.parseBoolean(props.getProperty("db.autocommit", "true"));
    }

    public static DatabaseUtil getInstance() {
        DatabaseUtil local = instance;
        if (local == null) {
            synchronized (DatabaseUtil.class) {
                local = instance;
                if (local == null) {
                    local = new DatabaseUtil();
                    instance = local;
                }
            }
        }
        return local;
    }


    public static Connection getConnection() {
        return getInstance().createConnection();
    }

    private Connection createConnection() {
        try {
            Connection connection = DriverManager.getConnection(url, user, password);
            connection.setAutoCommit(autoCommit);
            return connection;
        } catch (SQLException e) {
            throw new RuntimeException("Error connecting to the database", e);
        }
    }

    private static Properties loadProperties(String resourceName) {
        Properties props = new Properties();
        try (InputStream is = DatabaseUtil.class.getClassLoader().getResourceAsStream(resourceName)) {
            if (is == null) {
                throw new IllegalStateException("Resource not found: " + resourceName);
            }
            props.load(is);
            return props;
        } catch (IOException e) {
            throw new IllegalStateException("Failed to load " + resourceName, e);
        }
    }

    private static String requireProperty(Properties props, String key) {
        String value = props.getProperty(key);
        if (value == null || value.isBlank()) {
            throw new IllegalStateException("Missing required property: " + key);
        }
        return value;
    }
}

