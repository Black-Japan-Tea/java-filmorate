package ru.yandex.practicum.filmorate;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Setter
@Getter
@ToString
@ConfigurationProperties("filmorate")
public class Configuration {
    public static final int FILM_DESCRIPTION_LENGTH = 200;
    private int defaultTopFilmCount;
}