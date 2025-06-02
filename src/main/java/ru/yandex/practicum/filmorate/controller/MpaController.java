package ru.yandex.practicum.filmorate.controller;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.dto.film.mpa.MpaRateResponseDTO;
import ru.yandex.practicum.filmorate.service.MpaRateService;

import java.util.Collection;

@RestController
@RequestMapping("/mpa")
@AllArgsConstructor
public class MpaController {
    private final MpaRateService mpaRateService;

    @GetMapping
    public Collection<MpaRateResponseDTO> getMpaRates() {
        return mpaRateService.getMpaRates();
    }

    @GetMapping("/{mpaId}")
    public MpaRateResponseDTO getMpaById(@PathVariable int mpaId) {
        return mpaRateService.getMpaRateDTOById(mpaId);
    }
}