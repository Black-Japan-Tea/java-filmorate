package ru.yandex.practicum.filmorate.mapper.film;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dto.film.mpa.MpaRateRequestInFilmDTO;
import ru.yandex.practicum.filmorate.dto.film.mpa.MpaRateResponseDTO;
import ru.yandex.practicum.filmorate.dto.film.mpa.MpaRateResponseInFilmDTO;
import ru.yandex.practicum.filmorate.model.MpaRate;

import java.util.ArrayList;
import java.util.Collection;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class MpaRateMapper {

    public MpaRate toMpaRate(MpaRateRequestInFilmDTO mpaRateRequestInFilmDTO) {
        return MpaRate.builder()
                .id(mpaRateRequestInFilmDTO.getId())
                .build();
    }

    public MpaRateResponseDTO toMpaRateResponseDTO(MpaRate mpaRate) {
        return buildToMpaRateResponseDTO(mpaRate);
    }

    public Collection<MpaRateResponseDTO> toMpaRateResponseDTO(Collection<MpaRate> mpaRates) {
        return mpaRates.stream()
                .map(this::buildToMpaRateResponseDTO)
                .collect(Collectors.toCollection(ArrayList::new));

    }

    public MpaRateResponseInFilmDTO toMpaRateResponseInFilmDTO(MpaRate mpaRate) {
        return MpaRateResponseInFilmDTO.builder()
                .id(mpaRate.getId())
                .name(mpaRate.getName())
                .build();
    }

    private MpaRateResponseDTO buildToMpaRateResponseDTO(MpaRate mpaRate) {
        return MpaRateResponseDTO.builder()
                .id(mpaRate.getId())
                .name(mpaRate.getName())
                .build();
    }
}