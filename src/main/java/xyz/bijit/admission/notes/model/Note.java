package xyz.bijit.admission.notes.model;

import java.time.LocalDateTime;

public record Note(
    Long id,
    String title,
    String content,
    LocalDateTime createdAt
) {
    // Constructor for creating a new note (without ID)
    public Note(String title, String content) {
        this(null, title, content, LocalDateTime.now());
    }

    // Constructor for creating a note with ID but without createdAt
    public Note(Long id, String title, String content) {
        this(id, title, content, LocalDateTime.now());
    }
} 