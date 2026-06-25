package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.user.InMemoryUserStorage;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class UserControllerTest {
    private final InMemoryUserStorage userStorage = new InMemoryUserStorage();
    private final UserService userService = new UserService(userStorage);
    private final UserController controller = new UserController(userService);

    @Test
    void shouldCreateUserWithValidFields() {
        User user = makeUser();

        User createdUser = controller.createUser(user);

        assertEquals(1, createdUser.getId());
        assertEquals(1, controller.getUsers().size());
    }

    @Test
    void shouldNotCreateUserWithBlankEmail() {
        User user = makeUser();
        user.setEmail(" ");

        assertThrows(ValidationException.class, () -> controller.createUser(user));
    }

    @Test
    void shouldNotCreateUserWithoutAtInEmail() {
        User user = makeUser();
        user.setEmail("mail.ru");

        assertThrows(ValidationException.class, () -> controller.createUser(user));
    }

    @Test
    void shouldNotCreateUserWithBlankLogin() {
        User user = makeUser();
        user.setLogin(" ");

        assertThrows(ValidationException.class, () -> controller.createUser(user));
    }

    @Test
    void shouldNotCreateUserWithSpacesInLogin() {
        User user = makeUser();
        user.setLogin("user login");

        assertThrows(ValidationException.class, () -> controller.createUser(user));
    }

    @Test
    void shouldUseLoginAsNameWhenNameIsBlank() {
        User user = makeUser();
        user.setName(" ");

        User createdUser = controller.createUser(user);

        assertEquals(user.getLogin(), createdUser.getName());
    }

    @Test
    void shouldNotCreateUserWithBirthdayInFuture() {
        User user = makeUser();
        user.setBirthday(LocalDate.now().plusDays(1));

        assertThrows(ValidationException.class, () -> controller.createUser(user));
    }

    @Test
    void shouldCreateUserWithBirthdayToday() {
        User user = makeUser();
        user.setBirthday(LocalDate.now());

        User createdUser = controller.createUser(user);

        assertEquals(LocalDate.now(), createdUser.getBirthday());
    }

    @Test
    void shouldNotCreateUserWithEmptyBody() {
        assertThrows(ValidationException.class, () -> controller.createUser(null));
    }

    @Test
    void shouldUpdateExistingUser() {
        User user = controller.createUser(makeUser());
        user.setName("Updated name");

        User updatedUser = controller.updateUser(user);

        assertEquals("Updated name", updatedUser.getName());
        assertTrue(controller.getUsers().contains(updatedUser));
    }

    private User makeUser() {
        User user = new User();
        user.setEmail("user@mail.ru");
        user.setLogin("user");
        user.setName("User");
        user.setBirthday(LocalDate.of(2000, 1, 1));
        return user;
    }
}
