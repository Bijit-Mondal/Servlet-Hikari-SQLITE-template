package xyz.bijit.admission.config;

public final class AppConstants {
    private AppConstants() {
        // Prevent instantiation
    }

    // Database Configuration
    public static final String DB_NAME = "notes.db";
    public static final String DB_URL = "jdbc:sqlite:" + DB_NAME;

    // Notes Table and Columns
    public static final String NOTE_TABLE = "notes";
    public static final String NOTE_COLUMN_ID = "id";
    public static final String NOTE_COLUMN_TITLE = "title";
    public static final String NOTE_COLUMN_CONTENT = "content";
    public static final String NOTE_COLUMN_CREATED_AT = "created_at";
} 