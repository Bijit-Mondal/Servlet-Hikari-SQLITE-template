package xyz.bijit.admission.db;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import xyz.bijit.admission.config.AppConstants;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class DatabaseManager implements AutoCloseable {
    private final HikariDataSource dataSource;
    private final List<String> schemaStatements;

    public DatabaseManager() {
        this.schemaStatements = new ArrayList<>();
        initializeSchemaStatements();
        this.dataSource = initializeDataSource();
        createSchema();
    }

    private void initializeSchemaStatements() {
        // Add all table creation statements here
        schemaStatements.add(CreateNotes.CREATE_NOTES_TABLE);
        // Add more table creation statements as needed
    }

    private HikariDataSource initializeDataSource() {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(AppConstants.DB_URL);
        config.setMaximumPoolSize(10);
        config.setMinimumIdle(5);
        config.setIdleTimeout(30000);
        config.setConnectionTimeout(30000);
        config.setAutoCommit(true);
        
        return new HikariDataSource(config);
    }

    private void createSchema() {
        try (Connection conn = getConnection();
            Statement stmt = conn.createStatement()) {
            
            for (String schemaStatement : schemaStatements) {
                stmt.execute(schemaStatement);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to create database schema", e);
        }
    }

    public Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }

    @Override
    public void close() {
        if (dataSource != null && !dataSource.isClosed()) {
            dataSource.close();
        }
    }
} 