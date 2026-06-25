package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FilmControllerTest {
    private final FilmController controller = new FilmController();

    @Test
    void shouldCreateFilmWithValidFields() {
        Film film = makeFilm();

        Film createdFilm = controller.createFilm(film);

        assertEquals(1, createdFilm.getId());
        assertEquals(1, controller.getFilms().size());
    }

    @Test
    void shouldNotCreateFilmWithBlankName() {
        Film film = makeFilm();
        film.setName(" ");

        assertThrows(ValidationException.class, () -> controller.createFilm(film));
    }

    @Test
    void shouldNotCreateFilmWithLongDescription() {
        Film film = makeFilm();
        film.setDescription("a".repeat(201));

        assertThrows(ValidationException.class, () -> controller.createFilm(film));
    }

    @Test
    void shouldNotCreateFilmWithReleaseDateBeforeCinemaBirthday() {
        Film film = makeFilm();
        film.setReleaseDate(LocalDate.of(1895, 12, 27));

        assertThrows(ValidationException.class, () -> controller.createFilm(film));
    }

    @Test
    void shouldCreateFilmWithReleaseDateOnCinemaBirthday() {
        Film film = makeFilm();
        film.setReleaseDate(LocalDate.of(1895, 12, 28));

        Film createdFilm = controller.createFilm(film);

        assertEquals(LocalDate.of(1895, 12, 28), createdFilm.getReleaseDate());
    }

    @Test
    void shouldNotCreateFilmWithNegativeDuration() {
        Film film = makeFilm();
        film.setDuration(-1);

        assertThrows(ValidationException.class, () -> controller.createFilm(film));
    }

    @Test
    void shouldNotCreateFilmWithEmptyBody() {
        assertThrows(ValidationException.class, () -> controller.createFilm(null));
    }

    @Test
    void shouldUpdateExistingFilm() {
        Film film = controller.createFilm(makeFilm());
        film.setName("Updated film");

        Film updatedFilm = controller.updateFilm(film);

        assertEquals("Updated film", updatedFilm.getName());
        assertTrue(controller.getFilms().contains(updatedFilm));
    }

    private Film makeFilm() {
        Film film = new Film();
        film.setName("Film");
        film.setDescription("Description");
        film.setReleaseDate(LocalDate.of(2000, 1, 1));
        film.setDuration(100);
        return film;
    }
}
