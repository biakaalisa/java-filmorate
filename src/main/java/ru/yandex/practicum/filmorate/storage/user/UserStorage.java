package ru.yandex.practicum.filmorate.storage.user;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.Optional;

public interface UserStorage {
    User create(User user);

    User update(User user);

    void delete(int id);

    Optional<User> getById(int id);

    Collection<User> getAll();

    boolean contains(int id);
}
