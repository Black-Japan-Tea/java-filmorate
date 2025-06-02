package ru.yandex.practicum.filmorate.storage.film;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dal.FilmRepository;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.MpaRate;

import java.util.Collection;
import java.util.Optional;

@Component
@Qualifier("filmDbStorage")
@RequiredArgsConstructor
public class FilmDbStorage implements FilmStorage {

    private final FilmRepository filmRepository;

    @Override
    public Long createFilm(Film newFilm) {
        Long newFilmId = filmRepository.createFilm(newFilm);

        if (newFilm.getGenres() != null) {
            filmRepository.addFilmGenres(newFilmId, newFilm.getGenres());
        }

        return newFilmId;
    }

    @Override
    public Collection<Film> getFilms() {
        Collection<Film> films = filmRepository.getAllFilms();

        addCollectionsToFilms(films);

        return films;
    }


    @Override
    public boolean updateFilm(Film filmToUpdate) {
        return filmRepository.updateFilm(filmToUpdate);
    }

    @Override
    public Optional<Film> getFilmById(long id) {
        Optional<Film> optionalFilm = filmRepository.getFilmById(id);
        optionalFilm.ifPresent(this::addCollectionsToFilm);
        return optionalFilm;
    }

    @Override
    public int getFilmsCount() {
        return filmRepository.getFilmsCount();
    }

    @Override
    public int addUserLike(long filmId, long userId) {
        return filmRepository.addFilmUserLikes(filmId, userId);
    }

    @Override
    public boolean deleteUserLike(long filmId, long userId) {
        return filmRepository.deleteUserLike(filmId, userId);
    }

    @Override
    public Collection<Film> getTopFilms(int count) {
        Collection<Film> films = filmRepository.getTopFilms(count);

        addCollectionsToFilms(films);

        return films;
    }

    @Override
    public Optional<MpaRate> getMpaRateById(int mpaId) {
        return filmRepository.getMpaRateById(mpaId);
    }

    @Override
    public Collection<MpaRate> getMpaRates() {
        return filmRepository.getMpaRates();
    }

    @Override
    public Optional<Genre> getGenreById(int mpaId) {
        return filmRepository.getGenreById(mpaId);
    }

    @Override
    public Collection<Genre> getGenres() {
        return filmRepository.getGenres();
    }


    private void addCollectionsToFilms(Collection<Film> films) {

        films.forEach(film -> film.setGenres(filmRepository.getFilmGenres(film.getId())));

        films.forEach(film -> film.setUsersLikes(filmRepository.getFilmUserLikes(film.getId())));
    }

    private void addCollectionsToFilm(Film film) {
        film.setGenres(filmRepository.getFilmGenres(film.getId()));

        film.setUsersLikes(filmRepository.getFilmUserLikes(film.getId()));
    }
}