package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

@Data
@EqualsAndHashCode(of = {"email"})
public class User {
    Long id;
    String name;

    @PastOrPresent(message = "Дата рождения не может быть в будущем.")
    LocalDate birthday;

    @NotNull(message = "Логин не может быть пустым")
    String login;

    @NotNull(message = "Электронная почта не может быть пустой")
    @Email(message = "Электронная почта должна быть в формате: example@example.example")
    String email;
}