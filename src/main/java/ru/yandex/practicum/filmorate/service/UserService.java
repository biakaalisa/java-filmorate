package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
public class UserService {
    private final UserStorage userStorage;

    @Autowired
    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public User createUser(User user) {
        prepareUser(user);
        return userStorage.create(user);
    }

    public User updateUser(User user) {
        getUserOrThrow(user.getId());
        prepareUser(user);
        return userStorage.update(user);
    }

    public Collection<User> getUsers() {
        return userStorage.getAll();
    }

    public User getUserById(int id) {
        return getUserOrThrow(id);
    }

    public void addFriend(int userId, int friendId) {
        validateDifferentUsers(userId, friendId);
        User user = getUserOrThrow(userId);
        User friend = getUserOrThrow(friendId);
        user.getFriends().add(friendId);
        friend.getFriends().add(userId);
        log.info("Пользователь {} добавил пользователя {} в друзья", userId, friendId);
    }

    public void removeFriend(int userId, int friendId) {
        validateDifferentUsers(userId, friendId);
        User user = getUserOrThrow(userId);
        User friend = getUserOrThrow(friendId);
        user.getFriends().remove(friendId);
        friend.getFriends().remove(userId);
        log.info("Пользователь {} удалил пользователя {} из друзей", userId, friendId);
    }

    public List<User> getFriends(int userId) {
        User user = getUserOrThrow(userId);
        return user.getFriends().stream()
                .map(this::getUserOrThrow)
                .toList();
    }

    public List<User> getCommonFriends(int userId, int otherId) {
        validateDifferentUsers(userId, otherId);
        User user = getUserOrThrow(userId);
        User otherUser = getUserOrThrow(otherId);
        Set<Integer> otherFriends = otherUser.getFriends();
        return user.getFriends().stream()
                .filter(otherFriends::contains)
                .map(this::getUserOrThrow)
                .collect(Collectors.toList());
    }

    private User getUserOrThrow(int id) {
        return userStorage.getById(id)
                .orElseThrow(() -> new NotFoundException("Пользователь с id " + id + " не найден"));
    }

    private void validateDifferentUsers(int userId, int otherId) {
        if (userId == otherId) {
            log.warn("Операция невозможна для одного и того же пользователя: id={}", userId);
            throw new ValidationException("Пользователи должны быть разными");
        }
    }

    private void prepareUser(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
    }
}
