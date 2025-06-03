package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.MpaRate;

import java.util.Collection;
import java.util.Optional;

public interface FilmStorage {

    Long createFilm(Film newFilm);

    Collection<Film> getFilms();

    boolean updateFilm(Film filmToUpdate);

    Optional<Film> getFilmById(long id);

    int getFilmsCount();

    int addUserLike(long filmId, long userId);

    boolean deleteUserLike(long filmId, long userId);

    Collection<Film> getTopFilms(int count);

    Optional<MpaRate> getMpaRateById(int mpaId);

    Collection<MpaRate> getMpaRates();

    Optional<Genre> getGenreById(int mpaId);

    Collection<Genre> getGenres();
}