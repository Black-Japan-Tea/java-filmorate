package ru.yandex.practicum.filmorate.dto.film;

import lombok.Builder;
import lombok.Data;
import ru.yandex.practicum.filmorate.dto.film.genre.GenreResponseInFilmDTO;
import ru.yandex.practicum.filmorate.dto.film.mpa.MpaRateResponseInFilmDTO;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
public class FilmResponseDTO {

    private long id;

    private String name;

    private String description;

    private LocalDate releaseDate;

    private int duration;

    private List<Long> usersLikes;

    private MpaRateResponseInFilmDTO mpa;

    private List<GenreResponseInFilmDTO> genres;

}