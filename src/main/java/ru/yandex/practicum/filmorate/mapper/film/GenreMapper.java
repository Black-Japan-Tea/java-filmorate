package ru.yandex.practicum.filmorate.mapper.film;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dto.film.genre.GenreRequestInFilmDTO;
import ru.yandex.practicum.filmorate.dto.film.genre.GenreResponseDTO;
import ru.yandex.practicum.filmorate.dto.film.genre.GenreResponseInFilmDTO;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.ArrayList;
import java.util.Collection;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class GenreMapper {

    public GenreResponseDTO toGenreResponseDTO(Genre genre) {
        return buildGenreResponseDTO(genre);
    }

    public Collection<GenreResponseDTO> toGenreResponseDTO(Collection<Genre> genres) {
        return genres.stream()
                .map(this::buildGenreResponseDTO)
                .collect(Collectors.toCollection(ArrayList::new));

    }

    public GenreResponseInFilmDTO toGenreResponseInFilmDTO(Genre genre) {
        return GenreResponseInFilmDTO.builder()
                .id(genre.getId())
                .name(genre.getName())
                .build();
    }

    public Genre toGenre(GenreRequestInFilmDTO genreRequestInFilmDTO) {
        return Genre.builder()
                .id(genreRequestInFilmDTO.getId())
                .build();
    }

    private GenreResponseDTO buildGenreResponseDTO(Genre genre) {
        return GenreResponseDTO.builder()
                .id(genre.getId())
                .name(genre.getName())
                .build();
    }
}