package ru.yandex.practicum.filmorate.storage.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
public class InMemoryUserStorage implements UserStorage {
    private final Map<Integer, User> users = new HashMap<>();
    private int nextId = 1;

    @Override
    public User create(User user) {
        user.setId(nextId++);
        users.put(user.getId(), user);
        log.info("Пользователь сохранён в хранилище: id={}", user.getId());
        return user;
    }

    @Override
    public User update(User user) {
        if (!contains(user.getId())) {
            throw new NotFoundException("Пользователь с id " + user.getId() + " не найден");
        }
        User oldUser = getById(user.getId());
        user.setFriends(oldUser.getFriends());
        users.put(user.getId(), user);
        log.info("Пользователь обновлён в хранилище: id={}", user.getId());
        return user;
    }

    @Override
    public void delete(int id) {
        if (!contains(id)) {
            throw new NotFoundException("Пользователь с id " + id + " не найден");
        }
        users.remove(id);
        log.info("Пользователь удалён из хранилища: id={}", id);
    }

    @Override
    public User getById(int id) {
        if (!contains(id)) {
            throw new NotFoundException("Пользователь с id " + id + " не найден");
        }
        return users.get(id);
    }

    @Override
    public Collection<User> getAll() {
        return new ArrayList<>(users.values());
    }

    @Override
    public boolean contains(int id) {
        return users.containsKey(id);
    }
}
