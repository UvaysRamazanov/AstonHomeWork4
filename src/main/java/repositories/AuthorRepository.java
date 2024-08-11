package repositories;

import models.Author;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.TypedQuery;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AuthorRepository implements UserRepository<Author> {
    private static final Logger logger = Logger.getLogger(AuthorRepository.class.getName());
    private final EntityManager entityManager;

    public AuthorRepository(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public List<Author> getAllUsers() {
        TypedQuery<Author> query = entityManager.createQuery("SELECT u FROM User u WHERE u.userType = 'Author'", Author.class);
        return query.getResultList();
    }

    @Override
    public Author getUserById(long id) {
        return entityManager.find(Author.class, id);
    }

    @Override
    public void addUser(Author author) {
        executeInTransaction(entityManager -> entityManager.persist(author),
                "Ошибка при добавлении автора");
    }

    @Override
    public void updateUser(Author author) {
        executeInTransaction(entityManager -> entityManager.merge(author),
                "Ошибка при обновлении автора");
    }

    @Override
    public void deleteUser(long id) {
        executeInTransaction(entityManager -> {
            Author author = entityManager.find(Author.class, id);
            if (author != null) {
                entityManager.remove(author);
            }
        }, "Ошибка при удалении автора");
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
