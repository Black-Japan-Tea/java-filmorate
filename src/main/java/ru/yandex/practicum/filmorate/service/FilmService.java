package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.Configuration;
import ru.yandex.practicum.filmorate.dto.film.FilmResponseDTO;
import ru.yandex.practicum.filmorate.dto.film.NewFilmRequestDTO;
import ru.yandex.practicum.filmorate.dto.film.UpdateFilmRequestDTO;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.StorageException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.mapper.film.FilmMapper;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Optional;

@Slf4j
@Service
public class FilmService {

    private final Configuration config;
    private final FilmStorage filmStorage;
    private final UserService userService;
    private final FilmMapper filmMapper;


    public FilmService(Configuration config,
                       @Qualifier("filmDbStorage") FilmStorage filmStorage,
                       UserService userService,
                       FilmMapper filmMapper) {

        this.config = config;
        this.filmStorage = filmStorage;
        this.userService = userService;
        this.filmMapper = filmMapper;
    }

    public FilmResponseDTO createFilm(NewFilmRequestDTO newFilmDTO) {

        Film newFilm = filmMapper.toFilm(newFilmDTO);

        validateFilmReleaseDate(newFilm);

        long newFilmId = filmStorage.createFilm(newFilm);
        if (newFilmId == 0) {
            throw new StorageException(newFilm + " wasn't created");
        }
        newFilm.setId(newFilmId);

        return filmMapper.toFilmResponseDTO(newFilm);
    }

    public Collection<FilmResponseDTO> getFilms() {
        log.info("{} films were read", filmStorage.getFilmsCount());
        Collection<Film> films = filmStorage.getFilms();

        return filmMapper.toFilmResponseDTO(films);
    }

    public FilmResponseDTO getFilmById(long filmId) {
        Optional<Film> optionalFilm = filmStorage.getFilmById(filmId);

        if (optionalFilm.isEmpty()) {
            throw new NotFoundException("Film with id=" + filmId + " not found");
        }

        return filmMapper.toFilmResponseDTO(optionalFilm.get());
    }

    public Collection<FilmResponseDTO> getTopFilms(Integer count) {
        if (count == null) {
            count = config.getDefaultTopFilmCount();
        }
        return filmMapper.toFilmResponseDTO(filmStorage.getTopFilms(count));
    }

    public FilmResponseDTO updateFilm(UpdateFilmRequestDTO filmToUpdateDTO) {

        Film filmToUpdate = filmMapper.toFilm(filmToUpdateDTO);

        validateFilmToUpdate(filmToUpdate);
        boolean wasUpdated = filmStorage.updateFilm(filmToUpdate);
        if (!wasUpdated) {
            throw new StorageException(filmToUpdate + " wasn't updated");
        }
        log.info("User with id={} was updated", filmToUpdate.getId());
        return filmMapper.toFilmResponseDTO(filmStorage.getFilmById(filmToUpdate.getId()).orElse(null));
    }

    private void validateFilmReleaseDate(Film film) {
        final LocalDate FIRST_FILM_RELEASE_DATE = LocalDate.of(1895, 12, 28);
        if (film.getReleaseDate().isBefore(FIRST_FILM_RELEASE_DATE)) {
            String message = "Film release date must be after 28/12/1895";
            log.warn(message);
            throw new ValidationException(message);
        }

    }

    public void addUserLike(long filmId, long userId) {
        checkFilmExist(filmId);
        userService.checkAndGetUserById(userId);

        int wasAdded = filmStorage.addUserLike(filmId, userId);
        if (wasAdded == 0) {
            throw new StorageException("Like by User with id=" + userId + " wasn't added to Film with id=" + filmId);
        }
    }

    public void deleteUserLike(long filmId, long userId) {
        checkFilmExist(filmId);
        checkFilmUserLikeExist(filmId, userId);

        boolean wasAdded = filmStorage.deleteUserLike(filmId, userId);
        if (!wasAdded) {
            throw new StorageException("Like by User with id=" + userId + " wasn't delete from Film with id=" + filmId);
        }
    }

    private void validateFilmToUpdate(Film film) {
        validateFilmReleaseDate(film);
        checkFilmExist(film.getId());
    }

    private void checkFilmExist(long filmId) {
        Optional<Film> optionalFilm = filmStorage.getFilmById(filmId);
        if (optionalFilm.isEmpty()) {
            String message = "Film with id=" + filmId + " not found";
            log.warn(message);
            throw new NotFoundException(message);
        }
    }

    private void checkFilmUserLikeExist(long filmId, long userLikeId) {
        Optional<Film> optionalFilm = filmStorage.getFilmById(filmId);

        if (optionalFilm.isPresent()) {
            if (optionalFilm.get().getUsersLikes() != null) {
                if (!optionalFilm.get().getUsersLikes().contains(userLikeId)) {
                    String message = "Like with User id=" + userLikeId + " not found";
                    log.warn(message);
                    throw new NotFoundException(message);
                }
            }
        }
    }
}