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
        log.debug("FilmService initialized with Configuration: {}, FilmStorage: {}, UserService: {}, FilmMapper: {}",
                config.getClass(), filmStorage.getClass(), userService.getClass(), filmMapper.getClass());
    }

    public FilmResponseDTO createFilm(NewFilmRequestDTO newFilmDTO) {
        log.info("Creating new film from DTO: {}", newFilmDTO);
        Film newFilm = filmMapper.toFilm(newFilmDTO);

        validateFilmReleaseDate(newFilm);

        long newFilmId = filmStorage.createFilm(newFilm);
        if (newFilmId == 0) {
            String errorMessage = newFilm + " wasn't created";
            log.error(errorMessage);
            throw new StorageException(errorMessage);
        }
        newFilm.setId(newFilmId);
        log.info("Successfully created film with ID: {}", newFilmId);

        return filmMapper.toFilmResponseDTO(newFilm);
    }

    public Collection<FilmResponseDTO> getFilms() {
        log.info("Getting all films");
        int filmsCount = filmStorage.getFilmsCount();
        log.debug("Total films in storage: {}", filmsCount);

        Collection<Film> films = filmStorage.getFilms();
        log.debug("Retrieved {} films", films.size());

        return filmMapper.toFilmResponseDTO(films);
    }

    public FilmResponseDTO getFilmById(long filmId) {
        log.info("Getting film by ID: {}", filmId);
        Optional<Film> optionalFilm = filmStorage.getFilmById(filmId);

        if (optionalFilm.isEmpty()) {
            String errorMessage = "Film with id=" + filmId + " not found";
            log.error(errorMessage);
            throw new NotFoundException(errorMessage);
        }

        Film film = optionalFilm.get();
        log.debug("Found film: {}", film);
        return filmMapper.toFilmResponseDTO(film);
    }

    public Collection<FilmResponseDTO> getTopFilms(Integer count) {
        int topCount = count != null ? count : config.getDefaultTopFilmCount();
        log.info("Getting top {} films", topCount);

        Collection<Film> topFilms = filmStorage.getTopFilms(topCount);
        log.debug("Retrieved {} top films", topFilms.size());

        return filmMapper.toFilmResponseDTO(topFilms);
    }

    public FilmResponseDTO updateFilm(UpdateFilmRequestDTO filmToUpdateDTO) {
        log.info("Updating film with DTO: {}", filmToUpdateDTO);
        Film filmToUpdate = filmMapper.toFilm(filmToUpdateDTO);

        validateFilmToUpdate(filmToUpdate);
        boolean wasUpdated = filmStorage.updateFilm(filmToUpdate);
        if (!wasUpdated) {
            String errorMessage = filmToUpdate + " wasn't updated";
            log.error(errorMessage);
            throw new StorageException(errorMessage);
        }

        log.info("Film with id={} was successfully updated", filmToUpdate.getId());
        Film updatedFilm = filmStorage.getFilmById(filmToUpdate.getId()).orElse(null);
        log.debug("Updated film details: {}", updatedFilm);

        return filmMapper.toFilmResponseDTO(updatedFilm);
    }

    private void validateFilmReleaseDate(Film film) {
        final LocalDate FIRST_FILM_RELEASE_DATE = LocalDate.of(1895, 12, 28);
        if (film.getReleaseDate().isBefore(FIRST_FILM_RELEASE_DATE)) {
            String message = "Film release date must be after 28/12/1895";
            log.warn("Validation failed for film {}: {}", film, message);
            throw new ValidationException(message);
        }
    }

    public void addUserLike(long filmId, long userId) {
        log.info("Adding like from user {} to film {}", userId, filmId);
        checkFilmExist(filmId);
        userService.checkAndGetUserById(userId);

        int wasAdded = filmStorage.addUserLike(filmId, userId);
        if (wasAdded == 0) {
            String errorMessage = "Like by User with id=" + userId + " wasn't added to Film with id=" + filmId;
            log.error(errorMessage);
            throw new StorageException(errorMessage);
        }
        log.info("Successfully added like from user {} to film {}", userId, filmId);
    }

    public void deleteUserLike(long filmId, long userId) {
        log.info("Removing like from user {} to film {}", userId, filmId);
        checkFilmExist(filmId);
        checkFilmUserLikeExist(filmId, userId);

        boolean wasDeleted = filmStorage.deleteUserLike(filmId, userId);
        if (!wasDeleted) {
            String errorMessage = "Like by User with id=" + userId + " wasn't deleted from Film with id=" + filmId;
            log.error(errorMessage);
            throw new StorageException(errorMessage);
        }
        log.info("Successfully removed like from user {} to film {}", userId, filmId);
    }

    private void validateFilmToUpdate(Film film) {
        log.debug("Validating film for update: {}", film);
        validateFilmReleaseDate(film);
        checkFilmExist(film.getId());
    }

    private void checkFilmExist(long filmId) {
        log.debug("Checking existence of film with ID: {}", filmId);
        Optional<Film> optionalFilm = filmStorage.getFilmById(filmId);
        if (optionalFilm.isEmpty()) {
            String message = "Film with id=" + filmId + " not found";
            log.error(message);
            throw new NotFoundException(message);
        }
    }

    private void checkFilmUserLikeExist(long filmId, long userLikeId) {
        log.debug("Checking like from user {} for film {}", userLikeId, filmId);
        Optional<Film> optionalFilm = filmStorage.getFilmById(filmId);

        if (optionalFilm.isPresent()) {
            if (optionalFilm.get().getUsersLikes() != null) {
                if (!optionalFilm.get().getUsersLikes().contains(userLikeId)) {
                    String message = "Like with User id=" + userLikeId + " not found";
                    log.error(message);
                    throw new NotFoundException(message);
                }
            }
        }
    }
}