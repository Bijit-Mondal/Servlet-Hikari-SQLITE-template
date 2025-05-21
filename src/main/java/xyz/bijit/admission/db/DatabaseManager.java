package xyz.bijit.admission.db;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import xyz.bijit.admission.config.AppConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class DatabaseManager implements AutoCloseable {
    private static final Logger logger = LoggerFactory.getLogger(DatabaseManager.class);
    private final HikariDataSource dataSource;
    private final List<String> schemaStatements;

    public DatabaseManager() {
        logger.info("Initializing DatabaseManager");
        this.schemaStatements = new ArrayList<>();
        initializeSchemaStatements();
        this.dataSource = initializeDataSource();
        createSchema();
        logger.info("DatabaseManager initialized successfully");
    }

    private void initializeSchemaStatements() {
        logger.debug("Initializing schema statements");
        schemaStatements.add(CreateNotes.CREATE_NOTES_TABLE);
        // Add more table creation statements as needed
    }

    private HikariDataSource initializeDataSource() {
        logger.debug("Initializing HikariCP data source");
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(AppConstants.DB_URL);
        config.setMaximumPoolSize(10);
        config.setMinimumIdle(5);
        config.setIdleTimeout(30000);
        config.setConnectionTimeout(30000);
        config.setAutoCommit(true);
        
        logger.debug("HikariCP configuration: maxPoolSize={}, minIdle={}, idleTimeout={}ms, connectionTimeout={}ms",
            config.getMaximumPoolSize(), config.getMinimumIdle(), config.getIdleTimeout(), config.getConnectionTimeout());
        
        return new HikariDataSource(config);
    }

    private void createSchema() {
        logger.info("Creating database schema");
        try (Connection conn = getConnection();
            Statement stmt = conn.createStatement()) {
            
            for (String schemaStatement : schemaStatements) {
                logger.debug("Executing schema statement: {}", schemaStatement);
                stmt.execute(schemaStatement);
            }
            logger.info("Database schema created successfully");
        } catch (SQLException e) {
            logger.error("Failed to create database schema", e);
            throw new RuntimeException("Failed to create database schema", e);
        }
    }

    public Connection getConnection() throws SQLException {
        logger.debug("Getting database connection");
        return dataSource.getConnection();
    }

    @Override
    public void close() {
        logger.info("Closing DatabaseManager");
        if (dataSource != null && !dataSource.isClosed()) {
            dataSource.close();
            logger.info("DatabaseManager closed successfully");
        }
    }
} 