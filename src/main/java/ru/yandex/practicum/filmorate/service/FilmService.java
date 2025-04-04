package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
public class FilmService {
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    @Autowired
    public FilmService(FilmStorage filmStorage, UserStorage userStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
    }

    public Collection<Film> getAllFilms() {
        log.info("Получение всех фильмов");
        return filmStorage.getAllFilms();
    }

    public Film getFilmById(Long id) {
        log.info("Получение фильма с id {}", id);
        Film film = filmStorage.getFilmById(id);
        return Optional.ofNullable(film)
                .orElseThrow(() -> new FilmNotFoundException("Фильм с id " + id + " не найден"));
    }

    public Film addFilm(Film film) {
        log.info("Добавление нового фильма: {}", film);
        validateFilm(film);
        return filmStorage.addFilm(film);
    }

    public Film updateFilm(Film film) {
        log.info("Обновление фильма: {}", film);
        validateFilm(film);
        Film filmToUpdate = filmStorage.updateFilm(film);
        return Optional.ofNullable(filmToUpdate)
                .orElseThrow(() -> new FilmNotFoundException("Фильм с id " + film.getId() + " не найден"));
    }

    public void addLike(Long filmId, Long userId) {
        log.info("Добавление лайка фильму {} от пользователя {}", filmId, userId);

        Film film = getFilmById(filmId);
        User user = userStorage.getUserById(userId);
        Optional.ofNullable(user).
                orElseThrow(() -> new UserNotFoundException("Пользователь с id " + userId + " не найден"));

        if (film.getLikes().contains(userId)) {
            log.warn("Пользователь {} уже ставил лайк фильму {}", userId, filmId);
            throw new ValidationException("Пользователь уже лайкнул этот фильм");
        }

        film.getLikes().add(userId);
        filmStorage.updateFilm(film);
        log.debug("Лайк добавлен. Текущие лайки фильма {}: {}", filmId, film.getLikes());
    }

    public void removeLike(Long filmId, Long userId) {
        log.info("Удаление лайка фильму {} от пользователя {}", filmId, userId);

        Film film = getFilmById(filmId);
        User user = userStorage.getUserById(userId);
        Optional.ofNullable(user).
                orElseThrow(() -> new UserNotFoundException("Пользователь с id " + userId + " не найден"));

        if (!film.getLikes().remove(userId)) {
            throw new ValidationException("Лайк от пользователя " + userId + " не найден для фильма " + filmId);
        }

        filmStorage.updateFilm(film);
    }

    public List<Film> getPopularFilms(int count) {
        log.info("Запрос {} самых популярных фильмов", count);

        return filmStorage.getAllFilms().stream()
                .sorted(Comparator.comparingInt((Film f) -> f.getLikes().size()).reversed())
                .limit(count > 0 ? count : 10)
                .collect(Collectors.toList());
    }

    private void validateFilm(Film film) {
        if (film == null) {
            throw new ValidationException("Фильм не может быть null");
        }
    }
}