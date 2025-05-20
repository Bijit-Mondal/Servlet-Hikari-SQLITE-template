package xyz.bijit.admission.config;

public final class AppConstants {
    private AppConstants() {
        // Prevent instantiation
    }

    // Database Configuration
    public static final String DB_NAME = "notes.db";
    public static final String DB_URL = "jdbc:sqlite:" + DB_NAME;
    
    // Table Names
    public static final String TABLE_NOTES = "notes";
    
    // Column Names
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_TITLE = "title";
    public static final String COLUMN_CONTENT = "content";
    public static final String COLUMN_CREATED_AT = "created_at";
    
    // SQL Statements
    public static final String CREATE_NOTES_TABLE = 
        "CREATE TABLE IF NOT EXISTS " + TABLE_NOTES + " (" +
        COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
        COLUMN_TITLE + " TEXT NOT NULL, " +
        COLUMN_CONTENT + " TEXT NOT NULL, " +
        COLUMN_CREATED_AT + " TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
        ")";
} 