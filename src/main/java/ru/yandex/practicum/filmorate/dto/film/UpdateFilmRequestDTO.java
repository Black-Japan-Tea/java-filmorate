package ru.yandex.practicum.filmorate.dto.film;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import org.hibernate.validator.constraints.Length;
import ru.yandex.practicum.filmorate.Configuration;
import ru.yandex.practicum.filmorate.dto.film.genre.GenreRequestInFilmDTO;
import ru.yandex.practicum.filmorate.dto.film.mpa.MpaRateRequestInFilmDTO;

import java.time.LocalDate;
import java.util.List;

@Data
public class UpdateFilmRequestDTO {
    private long id;

    @NotBlank
    private String name;

    @Length(max = Configuration.FILM_DESCRIPTION_LENGTH)
    private String description;

    @NotNull
    private LocalDate releaseDate;

    @Positive
    private int duration;

    private MpaRateRequestInFilmDTO mpa;

    private List<GenreRequestInFilmDTO> genres;

    private List<Long> usersLikes;
}