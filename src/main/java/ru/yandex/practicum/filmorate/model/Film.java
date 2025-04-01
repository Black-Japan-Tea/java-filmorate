package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDate;

@Data
public class Film {
    Long id;

    @NotNull(message = "Дата релиза должна быть указана.")
    LocalDate releaseDate;

    @Positive(message = "Продолжительность не может отрицательным числом.")
    int duration;

    @Size(max = 200, message = "Максимальная длина описания — 200 символов.")
    String description;

    @NotNull(message = "Название не может быть пустым.")
    @NotBlank(message = "Название не может быть пустым.")
    String name;
}