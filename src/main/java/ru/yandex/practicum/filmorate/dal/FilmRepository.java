package ru.yandex.practicum.filmorate.dal;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.MpaRate;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class FilmRepository {

    protected final JdbcTemplate jdbcTemplate;
    private final RowMapper<Film> filmRowMapper;
    private final RowMapper<MpaRate> mpaRateRowMapper;
    private final RowMapper<Genre> genreRowMapper;

    public long createFilm(Film newFilm) {

        if (isMpaRateIndexNotOK(newFilm.getMpaRate().getId())) {
            throw new NotFoundException("MPA_RATE index = " + newFilm.getMpaRate().getId() + " not found");
        }

        String sqlString = "INSERT INTO FILMS(NAME, DESCRIPTION, RELEASE_DATE, DURATION, MPA_RATE) " +
                "values (?, ?, ?, ?, ?)";

        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(sqlString, new String[]{"FILM_ID"});
            stmt.setString(1, newFilm.getName());
            stmt.setString(2, newFilm.getDescription());
            stmt.setDate(3, Date.valueOf(newFilm.getReleaseDate()));
            stmt.setInt(4, newFilm.getDuration());
            stmt.setInt(5, newFilm.getMpaRate().getId());
            return stmt;
        }, keyHolder);

        return Objects.requireNonNull(keyHolder.getKey()).longValue();
    }

    public boolean updateFilm(Film filmToUpdate) {

        if (isMpaRateIndexNotOK(filmToUpdate.getMpaRate().getId())) {
            throw new NotFoundException("MPA_RATE index = " + filmToUpdate.getMpaRate().getId() + " not found");
        }

        String sqlString = "UPDATE FILMS SET NAME=?, DESCRIPTION=?, RELEASE_DATE=?, DURATION=?, MPA_RATE=?  WHERE FILM_ID=" + filmToUpdate.getId();

        int answer = jdbcTemplate.update(sqlString,
                filmToUpdate.getName(),
                filmToUpdate.getDescription(),
                filmToUpdate.getReleaseDate(),
                filmToUpdate.getDuration(),
                filmToUpdate.getMpaRate().getId()
        );

        return answer == 1;
    }

    public int getFilmsCount() {
        String sqlString = "SELECT Count(film_id) FROM FILMS";
        Integer count;

        count = jdbcTemplate.queryForObject(sqlString, Integer.class);

        if (count == null) {
            return 0;
        }
        return count;
    }


    public Optional<Film> getFilmById(long filmId) {
        String queryFilm = "SELECT FILM_ID, NAME, DESCRIPTION, RELEASE_DATE, DURATION, MPA_RATE FROM FILMS WHERE film_id=?";


        Optional<Film> optionalFilm;
        optionalFilm = Optional.ofNullable(jdbcTemplate.queryForObject(queryFilm, filmRowMapper, filmId));

        if (optionalFilm.isPresent()) {

            Film film = optionalFilm.get();

            Optional<MpaRate> optionalMpaRate = getMpaRateById(film.getMpaRate().getId());
            optionalMpaRate.ifPresent(film::setMpaRate);

        }

        return optionalFilm;
    }


    private List<Long> getFilmLikes(Long filmId) {
        String queryLikes = "SELECT USER_ID FROM FILMS_LIKES WHERE film_id=?";

        return jdbcTemplate.queryForList(queryLikes, Long.class, filmId);
    }

    public Optional<MpaRate> getMpaRateById(int mpaId) {
        String queryMpaRate = "SELECT mpa_id, name FROM MPA_RATE WHERE MPA_ID=?";

        List<MpaRate> result = jdbcTemplate.query(queryMpaRate, mpaRateRowMapper, mpaId);

        return result.stream().findFirst();

    }

    public Collection<MpaRate> getMpaRates() {
        String queryMpaRates = "SELECT mpa_id, name FROM MPA_RATE";
        return jdbcTemplate.query(queryMpaRates, mpaRateRowMapper);
    }

    public Optional<Genre> getGenreById(int genreId) {
        String queryGenre = "SELECT genre_id, name FROM GENRES WHERE GENRE_ID=?";
        List<Genre> result = jdbcTemplate.query(queryGenre, genreRowMapper, genreId);

        return result.stream().findFirst();
    }

    public Collection<Genre> getGenres() {
        String queryGenre = "SELECT genre_id, name FROM GENRES";

        return jdbcTemplate.query(queryGenre, genreRowMapper);
    }


    public void addFilmGenres(Long filmId, List<Genre> genres) {
        String sqlString = "INSERT INTO FILMS_GENRES(FILM_ID, GENRE_ID) " +
                "values (?, ?)";

        genres.stream()
                .distinct()
                .forEach(genre -> {
                    if (!isGenreIndexOK(genre.getId())) {
                        throw new NotFoundException("GENRE_ID index = " + genre.getId() + " not found");
                    }
                    jdbcTemplate.update(sqlString, filmId, genre.getId());

                });
    }

    public List<Genre> getFilmGenres(Long filmId) {
        String queryFilmGenres = "SELECT g.GENRE_ID, g.NAME FROM FILMS_GENRES fg " +
                "JOIN GENRES g ON fg.GENRE_ID = g.GENRE_ID " +
                "WHERE fg.FILM_ID=?";

        return jdbcTemplate.query(queryFilmGenres, genreRowMapper, filmId);
    }

    public int addFilmUserLikes(Long filmId, Long userId) {
        String sqlString = "INSERT INTO FILMS_LIKES(FILM_ID, USER_ID) " +
                "values (?, ?)";

        return jdbcTemplate.update(sqlString, filmId, userId);
    }

    public List<Long> getFilmUserLikes(long filmId) {
        String queryFilmUserLikes = "SELECT USER_ID FROM FILMS_LIKES WHERE FILM_ID=?";

        return jdbcTemplate.queryForList(queryFilmUserLikes, Long.class, filmId);
    }

    public Collection<Film> getAllFilms() {
        String sqlString = "SELECT FILM_ID, NAME, DESCRIPTION, RELEASE_DATE, DURATION, MPA_RATE FROM FILMS";

        return jdbcTemplate.query(sqlString, filmRowMapper);
    }

    private boolean isMpaRateIndexNotOK(int mpaRateIndex) {
        String sqlString = "SELECT Count(MPA_ID) FROM MPA_RATE";
        Integer count;


        count = jdbcTemplate.queryForObject(sqlString, Integer.class);

        if (count == null) {
            return false;
        }

        return mpaRateIndex > count;
    }

    private boolean isGenreIndexOK(int genreIndex) {
        String sqlString = "SELECT Count(GENRE_ID) FROM GENRES";
        Integer count;

        count = jdbcTemplate.queryForObject(sqlString, Integer.class);

        if (count == null) {
            return false;
        }

        return !(genreIndex > count);

    }

    public Collection<Film> getTopFilms(int count) {
        String getTopSql = "SELECT f.* FROM FILMS f " +
                "JOIN FILMS_LIKES fl ON f.FILM_ID = fl.FILM_ID " +
                "GROUP BY f.FILM_ID " +
                "ORDER BY COUNT(USER_ID) DESC " +
                "LIMIT ?";
        return jdbcTemplate.query(getTopSql, filmRowMapper, count);
    }

    public boolean deleteUserLike(long filmId, long userId) {
        String deleteUserLikeSql = "DELETE FROM FILMS_LIKES " +
                "WHERE FILM_ID=? AND USER_ID=?";

        return jdbcTemplate.update(deleteUserLikeSql, filmId, userId) > 0;
    }
}