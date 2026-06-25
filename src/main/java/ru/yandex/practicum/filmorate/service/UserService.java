package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
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
        return userStorage.create(user);
    }

    public User updateUser(User user) {
        return userStorage.update(user);
    }

    public Collection<User> getUsers() {
        return userStorage.getAll();
    }

    public User getUserById(int id) {
        return userStorage.getById(id);
    }

    public void addFriend(int userId, int friendId) {
        User user = userStorage.getById(userId);
        User friend = userStorage.getById(friendId);
        user.getFriends().add(friendId);
        friend.getFriends().add(userId);
        log.info("Пользователь {} добавил пользователя {} в друзья", userId, friendId);
    }

    public void removeFriend(int userId, int friendId) {
        User user = userStorage.getById(userId);
        User friend = userStorage.getById(friendId);
        user.getFriends().remove(friendId);
        friend.getFriends().remove(userId);
        log.info("Пользователь {} удалил пользователя {} из друзей", userId, friendId);
    }

    public List<User> getFriends(int userId) {
        User user = userStorage.getById(userId);
        return user.getFriends().stream()
                .map(userStorage::getById)
                .toList();
    }

    public List<User> getCommonFriends(int userId, int otherId) {
        User user = userStorage.getById(userId);
        User otherUser = userStorage.getById(otherId);
        Set<Integer> otherFriends = otherUser.getFriends();
        return user.getFriends().stream()
                .filter(otherFriends::contains)
                .map(userStorage::getById)
                .collect(Collectors.toList());
    }
}
