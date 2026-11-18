package javaapplication2;

import java.util.List;

public interface NoteService {
    List<Note> getNotesByUser(Long userId, String keyword);
    void saveNote(User user, String title, String content);
    void updateNote(Long noteId, Long userId, String title, String content);
    void deleteNote(Long noteId, Long userId);
}
