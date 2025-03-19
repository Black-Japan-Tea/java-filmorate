package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDate;

@Data
public class Film {
    LocalDate releaseDate;
    Long id;

    @NotNull(message = "Продолжительность не может быть пустой.")
    int duration;

    @NotNull(message = "Название не может быть пустым.")
    @NotBlank(message = "Название не может быть пустым.")
    String name;

    @Size(max = 200, message = "Максимальная длина описания — 200 символов.")
    String description;
}