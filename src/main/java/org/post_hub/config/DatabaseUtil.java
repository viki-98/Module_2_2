package org.post_hub.config;

import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
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

    public static void commit() {
        try {
            getInstance().createConnection().commit();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    private static Connection getConnection(boolean commit) {
        Connection connection = getInstance().createConnection();
        try {
            connection.setAutoCommit(commit);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return connection;
    }

    public static Connection getConnectionForLB() {
        return getInstance().createConnection();
    }

    public static PreparedStatement getPreparedStatementWithAutoCommit(String query) {
        try {
            return getConnection(true).prepareStatement(query);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static PreparedStatement getPreparedStatementWithoutAutoCommit(String query) {
        try {
            return getConnection(false).prepareStatement(query);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static PreparedStatement getPreparedStatementGetGeneratedKeys(String query) {
        try {
            return getConnection(false).prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static void rollback() {
        try {
            getInstance().createConnection().rollback();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
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

