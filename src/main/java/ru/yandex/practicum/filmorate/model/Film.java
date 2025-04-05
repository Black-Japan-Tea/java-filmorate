package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import lombok.Getter;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Getter
@Data
public class Film {
    private final Set<Long> likes = new HashSet<>();
    Long id;
    LocalDate releaseDate;
    int duration;
    String description;
    String name;
}