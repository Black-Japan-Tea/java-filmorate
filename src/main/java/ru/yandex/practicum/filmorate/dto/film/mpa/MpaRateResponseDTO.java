package ru.yandex.practicum.filmorate.dto.film.mpa;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MpaRateResponseDTO {
    private int id;
    private String name;
}