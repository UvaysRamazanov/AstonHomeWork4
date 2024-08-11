package repositories;

import models.User;
import java.util.List;

public interface UserRepository<T extends User> {
    List<T> getAllUsers();
    T getUserById(long id);
    void addUser(T user);
    void updateUser(T user);
    void deleteUser(long id);
}
