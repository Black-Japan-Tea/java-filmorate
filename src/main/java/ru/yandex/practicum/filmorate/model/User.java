package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import lombok.Data;

import java.time.LocalDate;

@Data
public class User {
    Long id;
    String name;

    @PastOrPresent(message = "Дата рождения не может быть в будущем.")
    LocalDate birthday;

    @NotBlank(message = "Логин не может быть пустым или содержать пробелы")
    String login;

    @NotNull(message = "Электронная почта не может быть пустой")
    @Email(message = "Электронная почта должна быть в формате: example@example.example")
    String email;
}