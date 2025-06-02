package ru.yandex.practicum.filmorate.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import lombok.Data;

import java.time.LocalDate;

@Data
public class UpdateUserRequestDTO {

    private long id;

    @NotNull
    @Email
    private String email;

    @NotBlank
    private String login;

    private String name;

    @NotNull
    @Past
    private LocalDate birthday;
}