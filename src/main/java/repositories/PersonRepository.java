package repositories;

import models.Person;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.TypedQuery;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PersonRepository implements UserRepository<Person> {
    private static final Logger logger = Logger.getLogger(PersonRepository.class.getName());
    private final EntityManager entityManager;

    public PersonRepository(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public List<Person> getAllUsers() {
        TypedQuery<Person> query = entityManager.createQuery("SELECT u FROM User u WHERE u.userType = 'Person'", Person.class);
        return query.getResultList();
    }

    @Override
    public Person getUserById(long id) {
        return entityManager.find(Person.class, id);
    }

    @Override
    public void addUser(Person person) {
        executeInTransaction(entityManager -> entityManager.persist(person),
                "Ошибка при добавлении персоны");
    }

    @Override
    public void updateUser(Person person) {
        executeInTransaction(entityManager -> entityManager.merge(person),
                "Ошибка при обновлении персоны");
    }

    @Override
    public void deleteUser(long id) {
        executeInTransaction(entityManager -> {
            Person person = entityManager.find(Person.class, id);
            if (person != null) {
                entityManager.remove(person);
            }
        }, "Ошибка при удалении персоны");
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
