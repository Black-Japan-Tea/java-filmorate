package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dto.film.genre.GenreResponseDTO;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.mapper.film.GenreMapper;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.Optional;

@Service
public class GenreService {
    private static final Logger log = LoggerFactory.getLogger(GenreService.class);

    private final FilmStorage filmStorage;
    private final GenreMapper genreMapper;

    public GenreService(@Qualifier("filmDbStorage") FilmStorage filmStorage, GenreMapper genreMapper) {
        this.filmStorage = filmStorage;
        this.genreMapper = genreMapper;
        log.debug("GenreService initialized with FilmStorage: {} and GenreMapper: {}", filmStorage.getClass(), genreMapper.getClass());
    }

    public GenreResponseDTO getGenreDTOById(int genreId) {
        log.info("Getting genre DTO by ID: {}", genreId);
        GenreResponseDTO response = genreMapper.toGenreResponseDTO(getGenreById(genreId));
        log.debug("Retrieved genre DTO: {}", response);
        return response;
    }

    protected Genre getGenreById(int genreId) {
        log.debug("Attempting to get genre by ID: {}", genreId);
        Optional<Genre> optionalGenre = filmStorage.getGenreById(genreId);

        if (optionalGenre.isPresent()) {
            Genre genre = optionalGenre.get();
            log.debug("Found genre: {}", genre);
            return genre;
        } else {
            String errorMessage = "Genre with id=" + genreId + " not found";
            log.error(errorMessage);
            throw new NotFoundException(errorMessage);
        }
    }

    public Collection<GenreResponseDTO> getGenres() {
        log.info("Getting all genres");
        Collection<GenreResponseDTO> genres = genreMapper.toGenreResponseDTO(filmStorage.getGenres());
        log.debug("Retrieved {} genres", genres.size());
        return genres;
    }
}