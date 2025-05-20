package xyz.bijit.admission.notes.service;

import xyz.bijit.admission.notes.model.Note;
import java.util.List;

public interface INoteService {
    List<Note> getAllNotes();
    Note addNote(String title, String content);
}