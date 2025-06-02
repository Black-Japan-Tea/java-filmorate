package ru.yandex.practicum.filmorate.controller.film;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.dto.film.FilmResponseDTO;
import ru.yandex.practicum.filmorate.dto.film.NewFilmRequestDTO;
import ru.yandex.practicum.filmorate.dto.film.UpdateFilmRequestDTO;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.util.Collection;

@RestController
@RequestMapping("/films")
@AllArgsConstructor
public class FilmController {

    private final FilmService filmService;


    @PostMapping
    public FilmResponseDTO createFilm(@RequestBody @Valid NewFilmRequestDTO newFilmDTO) {
        return filmService.createFilm(newFilmDTO);
    }

    @PutMapping
    public FilmResponseDTO updateFilm(@RequestBody @Valid UpdateFilmRequestDTO filmToUpdate) {
        return filmService.updateFilm(filmToUpdate);
    }

    @GetMapping
    public Collection<FilmResponseDTO> getFilms() {
        return filmService.getFilms();
    }

    @GetMapping("/{filmId}")
    public FilmResponseDTO getFilById(@PathVariable long filmId) {
        return filmService.getFilmById(filmId);
    }


    @GetMapping("/popular")
    public Collection<FilmResponseDTO> getTopFilms(@RequestParam(required = false) Integer count) {
        return filmService.getTopFilms(count);
    }


    @PutMapping("/{filmId}/like/{userId}")
    public void addUserLike(@PathVariable long filmId, @PathVariable long userId) {
        filmService.addUserLike(filmId, userId);
    }

    @DeleteMapping("/{filmId}/like/{userId}")
    public void deleteUserLike(@PathVariable long filmId, @PathVariable long userId) {
        filmService.deleteUserLike(filmId, userId);
    }

}