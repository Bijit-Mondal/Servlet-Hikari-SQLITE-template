package xyz.bijit.admission.db;

import xyz.bijit.admission.config.AppConstants;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseManager {
    private static DatabaseManager instance;
    private Connection connection;

    private DatabaseManager() {
        initializeDatabase();
    }

    public static synchronized DatabaseManager getInstance() {
        if (instance == null) {
            instance = new DatabaseManager();
        }
        return instance;
    }

    private void initializeDatabase() {
        try {
            connection = DriverManager.getConnection(AppConstants.DB_URL);
            createSchema();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to initialize database", e);
        }
    }

    private void createSchema() throws SQLException {
        try (Statement stmt = connection.createStatement()) {
            stmt.execute(AppConstants.CREATE_NOTES_TABLE);
        }
    }

    public Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                connection = DriverManager.getConnection(AppConstants.DB_URL);
            }
            return connection;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to get database connection", e);
        }
    }

    public void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to close database connection", e);
        }
    }
} 