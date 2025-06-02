package ru.yandex.practicum.filmorate.dto.film.genre;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class GenreResponseInFilmDTO {
    private int id;

    @NotNull
    private String name;
}