package repositories;

import models.Book;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.TypedQuery;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class BookRepository {
    private static final Logger logger = Logger.getLogger(BookRepository.class.getName());
    private final EntityManager entityManager;

    public BookRepository(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    // Метод для добавления новой книги
    public void addBook(Book book) {
        executeInTransaction(entityManager -> entityManager.persist(book),
                "Ошибка при добавлении книги");
    }

    // Метод для получения книги по ID
    public Book getBook(long id) {
        return entityManager.find(Book.class, id);
    }

    // Метод для получения всех книг
    public List<Book> getAllBooks() {
        TypedQuery<Book> query = entityManager.createQuery("SELECT b FROM Book b", Book.class);
        return query.getResultList();
    }

    // Метод для обновления книги
    public void updateBook(Book book) {
        executeInTransaction(entityManager -> entityManager.merge(book),
                "Ошибка при обновлении книги");
    }

    // Метод для удаления книги
    public void deleteBook(long id) {
        executeInTransaction(entityManager -> {
            Book book = entityManager.find(Book.class, id);
            if (book != null) {
                entityManager.remove(book);
            }
        }, "Ошибка при удалении книги");
    }

    private void executeInTransaction(EntityOperation operation, String errorMessage) {
        EntityTransaction transaction = entityManager.getTransaction();
        try {
            transaction.begin();
            operation.execute(entityManager);
            transaction.commit();
        } catch (Exception e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            logger.log(Level.SEVERE, errorMessage, e);
        }
    }

    @FunctionalInterface
    private interface EntityOperation {
        void execute(EntityManager entityManager);
    }
}
