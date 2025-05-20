package xyz.bijit.admission.notes.api;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import xyz.bijit.admission.notes.model.Note;
import xyz.bijit.admission.notes.service.NoteService;
import xyz.bijit.admission.notes.service.INoteService;
import xyz.bijit.admission.server.WebServerAPI;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;

public class NoteAPI implements WebServerAPI {
    private final INoteService noteService;
    private final Gson gson;

    public NoteAPI() {
        this.noteService = new NoteService();
        this.gson = new Gson();
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

    public void handleGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            response.setContentType("application/json");
            response.getWriter().write(gson.toJson(noteService.getAllNotes()));
        } catch (Exception e) {
            handleError(response, e);
        }
    }

    public void handlePost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try (BufferedReader reader = request.getReader()) {
            JsonObject json = gson.fromJson(reader, JsonObject.class);
            String title = json.get("title").getAsString();
            String content = json.get("content").getAsString();

            Note note = noteService.addNote(title, content);
            if (note != null) {
                response.setStatus(HttpServletResponse.SC_CREATED);
                response.setContentType("application/json");
                response.getWriter().write(gson.toJson(note));
            } else {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                response.getWriter().write("{\"error\": \"Failed to create note\"}");
            }
        } catch (Exception e) {
            handleError(response, e);
        }
    }

    private void handleError(HttpServletResponse response, Exception e) throws IOException {
        response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        response.setContentType("application/json");
        JsonObject error = new JsonObject();
        error.addProperty("error", e.getMessage());
        response.getWriter().write(gson.toJson(error));
    }

    @Override
    public String getApiPath() {
        return "/api/notes";
    }
} 