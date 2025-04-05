package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import lombok.Getter;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Getter
@Data
public class User {
    private final Set<Long> friends = new HashSet<>();
    Long id;
    String name;
    LocalDate birthday;
    String login;
    String email;
}