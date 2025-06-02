package ru.yandex.practicum.filmorate.controller;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.dto.film.genre.GenreResponseDTO;
import ru.yandex.practicum.filmorate.service.GenreService;

import java.util.Collection;

@RestController
@RequestMapping("/genres")
@AllArgsConstructor
public class GenreController {
    private final GenreService genreService;

    @GetMapping
    public Collection<GenreResponseDTO> getGenres() {
        return genreService.getGenres();
    }

    @GetMapping("/{genreId}")
    public GenreResponseDTO getGenreById(@PathVariable int genreId) {
        return genreService.getGenreDTOById(genreId);
    }
}