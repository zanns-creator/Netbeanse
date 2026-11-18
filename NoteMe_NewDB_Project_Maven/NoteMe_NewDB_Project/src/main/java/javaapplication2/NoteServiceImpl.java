package javaapplication2;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service("noteService")
public class NoteServiceImpl implements NoteService {

    @Override
    public List<Note> getNotesByUser(Long userId, String keyword) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            String hql = "FROM Note WHERE user.id = :uid";
            if (keyword != null && !keyword.trim().isEmpty()) {
                hql += " AND (title LIKE :kw OR content LIKE :kw)";
            }
            hql += " ORDER BY updatedAt DESC";

            Query<Note> query = session.createQuery(hql, Note.class);
            query.setParameter("uid", userId);
            if (keyword != null && !keyword.trim().isEmpty()) {
                query.setParameter("kw", "%" + keyword.trim() + "%");
            }
            return query.list();
        }
    }

    @Override
    public void saveNote(User user, String title, String content) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            Note note = new Note(user, title, content);
            session.save(note);
            tx.commit();
        } catch (Exception ex) {
            if (tx != null) tx.rollback();
            throw new RuntimeException("Gagal menambah catatan: " + ex.getMessage());
        }
    }

    @Override
    public void updateNote(Long noteId, Long userId, String title, String content) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            Note note = session.get(Note.class, noteId);
            if (note != null && note.getUser().getId().equals(userId)) {
                note.setTitle(title);
                note.setContent(content);
                note.setUpdatedAt(new Date());
                session.update(note);
                tx.commit();
            } else {
                throw new RuntimeException("Catatan tidak ditemukan atau tidak memiliki hak akses.");
            }
        } catch (Exception ex) {
            if (tx != null) tx.rollback();
            throw new RuntimeException("Gagal memperbarui catatan: " + ex.getMessage());
        }
    }

    @Override
    public void deleteNote(Long noteId, Long userId) {
        Transaction tx = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            Note note = session.get(Note.class, noteId);
            if (note != null && note.getUser().getId().equals(userId)) {
                session.delete(note);
                tx.commit();
            } else {
                throw new RuntimeException("Catatan tidak ditemukan atau tidak memiliki hak akses.");
            }
        } catch (Exception ex) {
            if (tx != null) tx.rollback();
            throw new RuntimeException("Gagal menghapus catatan: " + ex.getMessage());
        }
    }
}
