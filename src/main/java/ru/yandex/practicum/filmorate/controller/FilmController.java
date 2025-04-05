package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.util.Collection;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/films")
public class FilmController {
    private final FilmService filmService;

    @Autowired
    public FilmController(FilmService filmService) {
        this.filmService = filmService;
    }

    @GetMapping
    public Collection<Film> getAllFilms() {
        log.info("Запрос на получение списка всех фильмов");
        Collection<Film> films = filmService.getAllFilms();
        log.info("Получено {} фильмов", films.size());
        return films;
    }

    @GetMapping("/{id}")
    public Film getFilmById(@PathVariable Long id) {
        log.info("Запрос на получение фильма с ID {}", id);
        Film film = filmService.getFilmById(id);
        log.info("Найден фильм: ID={}, Название={}", film.getId(), film.getName());
        return film;
    }

    @PostMapping
    public Film addFilm(@RequestBody Film film) {
        log.info("Запрос на добавление нового фильма: {}", film);
        Film createdFilm = filmService.addFilm(film);
        log.info("Фильм успешно добавлен: ID={}, Название={}", createdFilm.getId(), createdFilm.getName());
        return createdFilm;
    }

    @PutMapping
    public Film updateFilm(@RequestBody Film film) {
        log.info("Запрос на обновление фильма с ID {}", film.getId());
        Film updatedFilm = filmService.updateFilm(film);
        log.info("Фильм успешно обновлен: ID={}, Название={}", updatedFilm.getId(), updatedFilm.getName());
        return updatedFilm;
    }

    @PutMapping("/{id}/like/{userId}")
    public void addLike(@PathVariable Long id, @PathVariable Long userId) {
        log.info("Запрос на добавление лайка: фильм ID={}, пользователь ID={}", id, userId);
        filmService.addLike(id, userId);
        log.info("Лайк успешно добавлен: фильм ID={}, пользователь ID={}", id, userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public void removeLike(@PathVariable Long id, @PathVariable Long userId) {
        log.info("Запрос на удаление лайка: фильм ID={}, пользователь ID={}", id, userId);
        filmService.removeLike(id, userId);
        log.info("Лайк успешно удален: фильм ID={}, пользователь ID={}", id, userId);
    }

    @GetMapping("/popular")
    public List<Film> getPopularFilms(@RequestParam(defaultValue = "10") Integer count) {
        log.info("Запрос на получение {} популярных фильмов", count);
        List<Film> popularFilms = filmService.getPopularFilms(count);
        log.info("Возвращено {} популярных фильмов", popularFilms.size());
        return popularFilms;
    }
}