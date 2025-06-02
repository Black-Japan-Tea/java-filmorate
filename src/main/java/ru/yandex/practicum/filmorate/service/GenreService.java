package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dto.film.genre.GenreResponseDTO;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.mapper.film.GenreMapper;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import java.util.Collection;
import java.util.Optional;

@Service
public class GenreService {

    private final FilmStorage filmStorage;
    private final GenreMapper genreMapper;

    public GenreService(@Qualifier("filmDbStorage") FilmStorage filmStorage, GenreMapper genreMapper) {
        this.filmStorage = filmStorage;
        this.genreMapper = genreMapper;
    }

    public GenreResponseDTO getGenreDTOById(int genreId) {
        return genreMapper.toGenreResponseDTO(getGenreById(genreId));
    }

    protected Genre getGenreById(int genreId) {

        Optional<Genre> optionalGenre = filmStorage.getGenreById(genreId);

        if (optionalGenre.isPresent()) {
            return optionalGenre.get();
        } else {
            throw new NotFoundException("Genre with id=" + genreId + "  not found");
        }
    }

    public Collection<GenreResponseDTO> getGenres() {
        return genreMapper.toGenreResponseDTO(filmStorage.getGenres());
    }
}