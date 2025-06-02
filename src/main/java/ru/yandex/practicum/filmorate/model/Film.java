package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@EqualsAndHashCode(of = "id")
public class Film {

    private long id;

    @NotBlank
    private String name;

    private String description;

    @NotNull
    private LocalDate releaseDate;

    @Positive
    private int duration;

    private List<Long> usersLikes;

    private MpaRate mpaRate;

    private List<Genre> genres;
}