package ru.yandex.practicum.filmorate.dto.film.genre;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class GenreResponseDTO {

    private int id;

    private String name;
}