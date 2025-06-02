package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dto.film.mpa.MpaRateResponseDTO;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.mapper.film.MpaRateMapper;
import ru.yandex.practicum.filmorate.model.MpaRate;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import java.util.Collection;
import java.util.Optional;

@Service
public class MpaRateService {

    private final FilmStorage filmStorage;
    private final MpaRateMapper mpaRateMapper;

    public MpaRateService(@Qualifier("filmDbStorage") FilmStorage filmStorage, MpaRateMapper mpaRateMapper) {
        this.filmStorage = filmStorage;
        this.mpaRateMapper = mpaRateMapper;
    }

    public MpaRateResponseDTO getMpaRateDTOById(int mapId) {
        return mpaRateMapper.toMpaRateResponseDTO(getMpaRateById(mapId));
    }

    protected MpaRate getMpaRateById(int mpaId) {

        Optional<MpaRate> optionalMpaRate = filmStorage.getMpaRateById(mpaId);

        if (optionalMpaRate.isPresent()) {
            return optionalMpaRate.get();
        } else {
            throw new NotFoundException("MPA rate with id=" + mpaId + "  not found");
        }
    }

    public Collection<MpaRateResponseDTO> getMpaRates() {
        return mpaRateMapper.toMpaRateResponseDTO(filmStorage.getMpaRates());
    }
}