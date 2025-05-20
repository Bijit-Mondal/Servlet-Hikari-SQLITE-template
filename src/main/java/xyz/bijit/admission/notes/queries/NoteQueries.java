package xyz.bijit.admission.notes.queries;

import xyz.bijit.admission.config.AppConstants;

public final class NoteQueries {
    private NoteQueries() {
        // Prevent instantiation
    }

    // Query builders
    public static String buildInsertQuery() {
        return String.format(
            "INSERT INTO %s (%s, %s) VALUES (?, ?)",
            AppConstants.NOTE_TABLE,
            AppConstants.NOTE_COLUMN_TITLE,
            AppConstants.NOTE_COLUMN_CONTENT
        );
    }

    public static String buildSelectAllQuery() {
        return String.format(
            "SELECT * FROM %s",
            AppConstants.NOTE_TABLE
        );
    }

    public static String buildSelectByIdQuery() {
        return String.format(
            "SELECT * FROM %s WHERE %s = ?",
            AppConstants.NOTE_TABLE,
            AppConstants.NOTE_COLUMN_ID
        );
    }

    public static String buildUpdateQuery() {
        return String.format(
            "UPDATE %s SET %s = ?, %s = ? WHERE %s = ?",
            AppConstants.NOTE_TABLE,
            AppConstants.NOTE_COLUMN_TITLE,
            AppConstants.NOTE_COLUMN_CONTENT,
            AppConstants.NOTE_COLUMN_ID
        );
    }

    public static String buildDeleteQuery() {
        return String.format(
            "DELETE FROM %s WHERE %s = ?",
            AppConstants.NOTE_TABLE,
            AppConstants.NOTE_COLUMN_ID
        );
    }
} 