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

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

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

    public void addLike(Long filmId, Long userId) {
        log.info("Добавление лайка фильму {} от пользователя {}", filmId, userId);

        // Проверяем существование фильма
        Optional<Film> filmOptional = Optional.ofNullable(filmStorage.getFilmById(filmId));
        if (filmOptional.isEmpty()) {
            throw new FilmNotFoundException("Фильм с id " + filmId + " не найден");
        }
        Film film = filmOptional.get();

        // Проверяем существование пользователя
        Optional<User> userOptional = Optional.ofNullable(userStorage.getUserById(userId));
        if (userOptional.isEmpty()) {
            throw new UserNotFoundException("Пользователь с id " + userId + " не найден");
        }

        // Проверяем, не ставил ли пользователь лайк ранее
        if (film.getLikes().contains(userId)) {
            log.warn("Пользователь {} уже ставил лайк фильму {}", userId, filmId);
            throw new ValidationException("Пользователь уже лайкнул этот фильм");
        }

        // Добавляем лайк
        film.getLikes().add(userId);
        log.debug("Текущие лайки фильма {}: {}", filmId, film.getLikes());
    }

    public void removeLike(Long filmId, Long userId) {
        // Проверяем существование фильма
        Optional<Film> filmOptional = Optional.ofNullable(filmStorage.getFilmById(filmId));
        if (filmOptional.isEmpty()) {
            throw new FilmNotFoundException("Фильм с id " + filmId + " не найден");
        }
        Film film = filmOptional.get();

        // Проверяем существование пользователя
        Optional<User> userOptional = Optional.ofNullable(userStorage.getUserById(userId));
        if (userOptional.isEmpty()) {
            throw new UserNotFoundException("Пользователь с id " + userId + " не найден");
        }

        // Удаляем лайк и проверяем его существование
        if (!film.getLikes().remove(userId)) {
            throw new ValidationException("Лайк от пользователя " + userId + " не найден для фильма " + filmId);
        }
    }

    public List<Film> getPopularFilms(int count) {
        log.info("Запрос {} самых популярных фильмов", count);
        List<Film> films = new ArrayList<>(filmStorage.getAllFilms());
        films.sort(Comparator.comparingInt(f -> -f.getLikes().size()));

        List<Film> result = films.subList(0, Math.min(count, films.size()));
        log.debug("Список популярных фильмов: {}", result);
        return result;
    }
}