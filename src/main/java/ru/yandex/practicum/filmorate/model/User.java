package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
public class User {
    private final Set<Long> friends = new HashSet<>();
    Long id;
    String name;
    @Past(message = "Дата рождения не может быть в будущем.")
    @NotNull(message = "Дата рождения должна быть указана.")
    LocalDate birthday;

    @NotBlank(message = "Логин не может быть пустым")
    @Pattern(regexp = "\\S+", message = "Логин не может содержать пробелы")
    String login;

    @NotNull(message = "Электронная почта не может быть пустой")
    @Email(message = "Электронная почта должна быть в формате: example@example.example")
    String email;
}