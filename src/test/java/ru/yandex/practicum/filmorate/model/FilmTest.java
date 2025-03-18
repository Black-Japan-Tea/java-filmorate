package ru.yandex.practicum.filmorate.model;

import org.junit.jupiter.api.Test;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

import java.time.Duration;
import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class FilmTest {

    private final ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    private final Validator validator = factory.getValidator();

    @Test
    void createValidFilm() {
        Film film = new Film();
        film.setName("Valid Film");
        film.setDescription("Valid Description");
        film.setReleaseDate(LocalDate.of(2000, 1, 1));
        film.setDuration(Duration.ofHours(2));

        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertTrue(violations.isEmpty(), "Валидный фильм не должен иметь нарушений валидации");
    }

    @Test
    void createFilmWithEmptyName() {
        Film film = new Film();
        film.setName("");
        film.setDescription("Valid Description");
        film.setReleaseDate(LocalDate.of(2000, 1, 1));
        film.setDuration(Duration.ofHours(2));

        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertFalse(violations.isEmpty(), "Пустое название фильма должно вызывать ошибку валидации");
    }

    @Test
    void createFilmWithLongDescription() {
        Film film = new Film();
        film.setName("Valid Film");
        film.setDescription("A".repeat(201));
        film.setReleaseDate(LocalDate.of(2000, 1, 1));
        film.setDuration(Duration.ofHours(2));

        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertFalse(violations.isEmpty(), "Описание длиннее 200 символов должно вызывать ошибку валидации");
    }

    @Test
    void createFilmWithInvalidReleaseDate() {
        Film film = new Film();
        film.setName("Valid Film");
        film.setDescription("Valid Description");
        film.setReleaseDate(LocalDate.of(1895, 12, 27));
        film.setDuration(Duration.ofHours(2));

        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertTrue(violations.isEmpty(), "Дата релиза раньше 28 декабря 1895 года должна вызывать ошибку валидации");
    }
}