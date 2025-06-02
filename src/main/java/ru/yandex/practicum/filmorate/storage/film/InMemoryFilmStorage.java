package ru.yandex.practicum.filmorate.storage.film;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.MpaRate;

import java.util.*;
import java.util.stream.Collectors;

@Component
@Qualifier("inMemoryFilmStorage")
public class InMemoryFilmStorage implements FilmStorage {

    private final HashMap<Long, Film> films = new HashMap<>();
    private Long filmId = 1L;

    @Override
    public Long createFilm(Film newFilm) {
        newFilm.setId(filmId++);
        newFilm.setUsersLikes(new ArrayList<>());
        films.put(newFilm.getId(), newFilm);
        return newFilm.getId();
    }

    @Override
    public Collection<Film> getFilms() {
        return films.values();
    }

    @Override
    public Collection<Film> getTopFilms(int count) {
        return films.values()
                .stream()
                .sorted(Comparator.comparingInt((Film film) -> film.getUsersLikes().size()).reversed())
                .limit(count)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<MpaRate> getMpaRateById(int mpaId) {
        return Optional.empty();
    }

    @Override
    public Collection<MpaRate> getMpaRates() {
        return List.of();
    }

    @Override
    public Optional<Genre> getGenreById(int mpaId) {
        return Optional.empty();
    }

    @Override
    public Collection<Genre> getGenres() {
        return List.of();
    }

    @Override
    public boolean updateFilm(Film filmToUpdate) {
        if (filmToUpdate.getUsersLikes() == null) {
            filmToUpdate.setUsersLikes(new ArrayList<>());
        }
        films.put(filmToUpdate.getId(), filmToUpdate);
        return true;
    }

    @Override
    public Optional<Film> getFilmById(long id) {
        if (films.containsKey(id)) {
            return Optional.of(films.get(id));
        }
        return Optional.empty();
    }

    @Override
    public int getFilmsCount() {
        return films.size();
    }

    @Override
    public int addUserLike(long filmId, long userId) {
        films.get(filmId).getUsersLikes().add(userId);
        return 1;
    }

    @Override
    public boolean deleteUserLike(long filmId, long userId) {
        films.get(filmId).getUsersLikes().remove(userId);
        return true;
    }
}