package ru.yandex.practicum.filmorate.storage.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
public class InMemoryFilmStorage implements FilmStorage {
    private final Map<Integer, Film> films = new HashMap<>();
    private int nextId = 1;

    @Override
    public Film create(Film film) {
        film.setId(nextId++);
        films.put(film.getId(), film);
        log.info("Фильм сохранён в хранилище: id={}", film.getId());
        return film;
    }

    @Override
    public Film update(Film film) {
        if (!contains(film.getId())) {
            throw new NotFoundException("Фильм с id " + film.getId() + " не найден");
        }
        Film oldFilm = getById(film.getId());
        film.setLikes(oldFilm.getLikes());
        films.put(film.getId(), film);
        log.info("Фильм обновлён в хранилище: id={}", film.getId());
        return film;
    }

    @Override
    public void delete(int id) {
        if (!contains(id)) {
            throw new NotFoundException("Фильм с id " + id + " не найден");
        }
        films.remove(id);
        log.info("Фильм удалён из хранилища: id={}", id);
    }

    @Override
    public Film getById(int id) {
        if (!contains(id)) {
            throw new NotFoundException("Фильм с id " + id + " не найден");
        }
        return films.get(id);
    }

    @Override
    public Collection<Film> getAll() {
        return new ArrayList<>(films.values());
    }

    @Override
    public boolean contains(int id) {
        return films.containsKey(id);
    }
}
