package xyz.bijit.admission.db;

import xyz.bijit.admission.config.AppConstants;

public final class CreateNotes {
    private CreateNotes() {
        // Prevent instantiation
    }

    public static final String CREATE_NOTES_TABLE = "CREATE TABLE IF NOT EXISTS " + AppConstants.NOTE_TABLE + " (" +
        AppConstants.NOTE_COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
        AppConstants.NOTE_COLUMN_TITLE + " TEXT NOT NULL, " +
        AppConstants.NOTE_COLUMN_CONTENT + " TEXT NOT NULL, " +
        AppConstants.NOTE_COLUMN_CREATED_AT + " TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
        ")";
}
