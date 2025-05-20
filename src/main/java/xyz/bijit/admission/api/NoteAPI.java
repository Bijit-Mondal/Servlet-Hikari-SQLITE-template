package xyz.bijit.admission.api;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import xyz.bijit.admission.config.AppConstants;
import xyz.bijit.admission.db.DatabaseManager;
import xyz.bijit.admission.model.Note;
import xyz.bijit.admission.server.WebServerAPI;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class NoteAPI implements WebServerAPI {
    private final DatabaseManager dbManager;
    private final Gson gson;

    public NoteAPI() {
        this.dbManager = DatabaseManager.getInstance();
        this.gson = new GsonBuilder()
            .registerTypeAdapter(LocalDateTime.class, (com.google.gson.JsonDeserializer<LocalDateTime>) (json, type, context) -> LocalDateTime.parse(json.getAsString()))
            .registerTypeAdapter(LocalDateTime.class, (com.google.gson.JsonSerializer<LocalDateTime>) (src, typeOfSrc, context) -> new com.google.gson.JsonPrimitive(src.toString()))
            .create();
    }

    @Override
    public void handleRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        switch (request.getMethod()) {
            case "GET":
                handleGet(request, response);
                break;
            case "POST":
                handlePost(request, response);
                break;
            default:
                response.setStatus(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
                response.getWriter().write("{\"error\": \"Method not allowed\"}");
        }
    }

    private void handleGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try (Connection conn = dbManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT * FROM " + AppConstants.TABLE_NOTES);
             ResultSet rs = stmt.executeQuery()) {

            List<Note> notes = new ArrayList<>();
            while (rs.next()) {
                Note note = new Note();
                note.setId(rs.getLong(AppConstants.COLUMN_ID));
                note.setTitle(rs.getString(AppConstants.COLUMN_TITLE));
                note.setContent(rs.getString(AppConstants.COLUMN_CONTENT));
                note.setCreatedAt(rs.getTimestamp(AppConstants.COLUMN_CREATED_AT).toLocalDateTime());
                notes.add(note);
            }

            response.getWriter().write(gson.toJson(notes));
        } catch (SQLException e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("{\"error\": \"Database error\"}");
        }
    }

    private void handlePost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try (BufferedReader reader = request.getReader()) {
            Note note = gson.fromJson(reader, Note.class);
            
            try (Connection conn = dbManager.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(
                     "INSERT INTO " + AppConstants.TABLE_NOTES + 
                     " (" + AppConstants.COLUMN_TITLE + ", " + AppConstants.COLUMN_CONTENT + ") VALUES (?, ?)")) {
                
                stmt.setString(1, note.getTitle());
                stmt.setString(2, note.getContent());
                stmt.executeUpdate();
                
                response.setStatus(HttpServletResponse.SC_CREATED);
                response.getWriter().write("{\"message\": \"Note created successfully\"}");
            }
        } catch (SQLException e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("{\"error\": \"Database error\"}");
        }
    }

    @Override
    public String getApiPath() {
        return "/api/notes";
    }
} 