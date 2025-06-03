package ru.yandex.practicum.filmorate.mapper.film;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dto.film.FilmResponseDTO;
import ru.yandex.practicum.filmorate.dto.film.NewFilmRequestDTO;
import ru.yandex.practicum.filmorate.dto.film.UpdateFilmRequestDTO;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.MpaRate;

import java.util.ArrayList;
import java.util.Collection;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class FilmMapper {
    private final GenreMapper genreMapper;
    private final MpaRateMapper mpaRateMapper;


    public Film toFilm(NewFilmRequestDTO filmDTO) {
        Film film = Film.builder()
                .name(filmDTO.getName())
                .description(filmDTO.getDescription())
                .releaseDate(filmDTO.getReleaseDate())
                .duration(filmDTO.getDuration())
                .build();

        if (filmDTO.getMpa() != null) {
            film.setMpaRate(MpaRate.builder()
                    .id(filmDTO.getMpa().getId())
                    .build());

        }

        if (filmDTO.getGenres() != null) {
            film.setGenres(filmDTO.getGenres()
                    .stream()
                    .map(genreMapper::toGenre)
                    .toList());
        }

        return film;
    }


    public Film toFilm(UpdateFilmRequestDTO filmDTO) {

        Film film = Film.builder()
                .id(filmDTO.getId())
                .name(filmDTO.getName())
                .description(filmDTO.getDescription())
                .releaseDate(filmDTO.getReleaseDate())
                .duration(filmDTO.getDuration())
                .build();

        if (filmDTO.getMpa() != null) {
            film.setMpaRate(MpaRate.builder()
                    .id(filmDTO.getMpa().getId())
                    .build());
        }

        if (filmDTO.getGenres() != null) {
            film.setGenres(filmDTO.getGenres()
                    .stream()
                    .map(genreMapper::toGenre)
                    .toList());
        }

        if (film.getUsersLikes() != null) {
            filmDTO.setUsersLikes(film.getUsersLikes()
                    .stream()
                    .toList());
        }

        return film;
    }

    public FilmResponseDTO toFilmResponseDTO(Film film) {
        return buildNewFilmResponseDTO(film);
    }


    public Collection<FilmResponseDTO> toFilmResponseDTO(Collection<Film> films) {
        return films.stream()
                .map(this::buildNewFilmResponseDTO)
                .collect(Collectors.toCollection(ArrayList::new));

    }

    private FilmResponseDTO buildNewFilmResponseDTO(Film film) {
        FilmResponseDTO filmDTO = FilmResponseDTO.builder()
                .id(film.getId())
                .name(film.getName())
                .description(film.getDescription())
                .releaseDate(film.getReleaseDate())
                .duration(film.getDuration())
                .usersLikes(film.getUsersLikes())
                .build();

        if (film.getMpaRate() != null) {
            filmDTO.setMpa(mpaRateMapper.toMpaRateResponseInFilmDTO(film.getMpaRate()));
        }

        if (film.getGenres() != null) {
            filmDTO.setGenres(film.getGenres()
                    .stream()
                    .map(genreMapper::toGenreResponseInFilmDTO)
                    .toList());
        }

        return filmDTO;
    }
}