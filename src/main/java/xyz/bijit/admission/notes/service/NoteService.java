package xyz.bijit.admission.notes.service;

import xyz.bijit.admission.notes.model.Note;
import xyz.bijit.admission.notes.queries.NoteQueries;
import xyz.bijit.admission.config.AppConstants;
import xyz.bijit.admission.db.DatabaseManager;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class NoteService implements INoteService {
    private final DatabaseManager dbManager;

    public NoteService(DatabaseManager dbManager) {
        this.dbManager = dbManager;
    }

    @Override
    public List<Note> getAllNotes() {
        List<Note> notes = new ArrayList<>();
        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(NoteQueries.buildSelectAllQuery());
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Note note = new Note(
                        rs.getLong(AppConstants.NOTE_COLUMN_ID),
                        rs.getString(AppConstants.NOTE_COLUMN_TITLE),
                        rs.getString(AppConstants.NOTE_COLUMN_CONTENT)
                );
                notes.add(note);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return notes;
    }

    @Override
    public Note addNote(String title, String content) {
        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(NoteQueries.buildInsertQuery(), Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, title);
            stmt.setString(2, content);
            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Creating note failed, no rows affected.");
            }
            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return new Note(
                            generatedKeys.getLong(1),
                            title,
                            content
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}