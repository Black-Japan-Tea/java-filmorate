package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dto.film.mpa.MpaRateResponseDTO;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.mapper.film.MpaRateMapper;
import ru.yandex.practicum.filmorate.model.MpaRate;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.Optional;

@Service
public class MpaRateService {
    private static final Logger log = LoggerFactory.getLogger(MpaRateService.class);

    private final FilmStorage filmStorage;
    private final MpaRateMapper mpaRateMapper;

    public MpaRateService(@Qualifier("filmDbStorage") FilmStorage filmStorage, MpaRateMapper mpaRateMapper) {
        this.filmStorage = filmStorage;
        this.mpaRateMapper = mpaRateMapper;
        log.debug("MpaRateService initialized with FilmStorage: {} and MpaRateMapper: {}",
                filmStorage.getClass(), mpaRateMapper.getClass());
    }

    public MpaRateResponseDTO getMpaRateDTOById(int mpaId) {
        log.info("Getting MPA rate DTO by ID: {}", mpaId);
        MpaRateResponseDTO response = mpaRateMapper.toMpaRateResponseDTO(getMpaRateById(mpaId));
        log.debug("Retrieved MPA rate DTO: {}", response);
        return response;
    }

    protected MpaRate getMpaRateById(int mpaId) {
        log.debug("Attempting to get MPA rate by ID: {}", mpaId);
        Optional<MpaRate> optionalMpaRate = filmStorage.getMpaRateById(mpaId);

        if (optionalMpaRate.isPresent()) {
            MpaRate mpaRate = optionalMpaRate.get();
            log.debug("Found MPA rate: {}", mpaRate);
            return mpaRate;
        } else {
            String errorMessage = "MPA rate with id=" + mpaId + " not found";
            log.error(errorMessage);
            throw new NotFoundException(errorMessage);
        }
    }

    public Collection<MpaRateResponseDTO> getMpaRates() {
        log.info("Getting all MPA rates");
        Collection<MpaRateResponseDTO> rates = mpaRateMapper.toMpaRateResponseDTO(filmStorage.getMpaRates());
        log.debug("Retrieved {} MPA rates", rates.size());
        return rates;
    }
}