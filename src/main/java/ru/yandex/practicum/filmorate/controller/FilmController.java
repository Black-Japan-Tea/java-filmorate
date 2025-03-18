package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.time.Month;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@RestController
@Slf4j
public class FilmController {

    private final Map<Long, Film> films = new HashMap<>();

    @GetMapping
    public Collection<Film> getAllFilms() {
        log.info("Запрос на отображение всех фильмов ({}).", films);
        return films.values();
    }

    @PostMapping
    public Film addFilm(@Valid @RequestBody Film film) {

        log.info("Запрос на добавление фильма с id = {}", film.getId());

        LocalDate cinemaBirthday = LocalDate.of(1895, Month.DECEMBER, 28);
        if (film.getReleaseDate().isBefore(cinemaBirthday)) {
            log.warn("Ошибка добавления нового фильма: дата релиза не может быть раньше 28 декабря 1895 года.",
                    new ValidationException("Дата релиза не может быть раньше 28 декабря 1895 года."));
        }

        if (film.getDuration().isZero() || film.getDuration().isNegative()) {
            log.warn("Ошибка добавления нового фильма: дата релиза не может быть отрицательной или равной нулю.",
                    new ValidationException("Дата релиза не может быть отрицательной или равной нулю."));
        }

        film.setId(getNextId());
        films.put(film.getId(), film);
        log.info("Фильм с id = {} успешно добавлен", film.getId());
        return film;
    }

    @PutMapping
    public Film updateFilm(@RequestBody Film newFilm) {

        log.info("Запрос на обновление фильма с id = {}", newFilm.getId());

        if (films.containsKey(newFilm.getId())) {

            Film oldFilm = films.get(newFilm.getId());

            if (!oldFilm.getName().equals(newFilm.getName())) {
                oldFilm.setName(newFilm.getName());
            }
            if (!oldFilm.getDescription().equals(newFilm.getDescription())) {
                oldFilm.setDescription(newFilm.getDescription());
            }
            if (!oldFilm.getReleaseDate().equals(newFilm.getReleaseDate())) {
                oldFilm.setReleaseDate(newFilm.getReleaseDate());
            }
            if (!oldFilm.getDuration().equals(newFilm.getDuration())) {
                oldFilm.setDuration(newFilm.getDuration());
            }
            log.info("Фильм с id = {} успешно обновлён", newFilm.getId());

            return oldFilm;
        }

        log.warn("Ошибка обновления данных о фильме. Фильма с id = {} нет.", newFilm.getId());
        throw new ValidationException("Фильм с id = " + newFilm.getId() + " не найден.");
    }

    private long getNextId() {
        long currentMaxId = films.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }
}