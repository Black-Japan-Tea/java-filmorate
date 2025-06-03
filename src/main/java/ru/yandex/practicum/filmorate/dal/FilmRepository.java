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
import java.util.*;

@Repository
@RequiredArgsConstructor
public class FilmRepository {

    protected final JdbcTemplate jdbcTemplate;
    private final RowMapper<Film> filmRowMapper;
    private final RowMapper<MpaRate> mpaRateRowMapper;
    private final RowMapper<Genre> genreRowMapper;

    public long createFilm(Film newFilm) {

        if (isMpaRateIndexNotOK(newFilm.getMpaRate().getId())) {
            throw new NotFoundException("mpa_rate index = " + newFilm.getMpaRate().getId() + " not found");
        }

        String sqlString = "INSERT INTO films(name, description, release_date, duration, mpa_rate) " +
                "values (?, ?, ?, ?, ?)";

        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(sqlString, new String[]{"film_id"});
            stmt.setString(1, newFilm.getName());
            stmt.setString(2, newFilm.getDescription());
            stmt.setDate(3, Date.valueOf(newFilm.getReleaseDate()));
            stmt.setInt(4, newFilm.getDuration());
            stmt.setInt(5, newFilm.getMpaRate().getId());
            return stmt;
        }, keyHolder);

        return keyHolder.getKey().longValue();
    }

    public boolean updateFilm(Film filmToUpdate) {

        if (isMpaRateIndexNotOK(filmToUpdate.getMpaRate().getId())) {
            throw new NotFoundException("mpa_rate index = " + filmToUpdate.getMpaRate().getId() + " not found");
        }

        String sqlString = "UPDATE films SET name=?, description=?, release_date=?, duration=?, mpa_rate=?  WHERE film_id=" + filmToUpdate.getId();

        int answer = jdbcTemplate.update(sqlString,
                filmToUpdate.getName(),
                filmToUpdate.getDescription(),
                filmToUpdate.getReleaseDate(),
                filmToUpdate.getDuration(),
                filmToUpdate.getMpaRate().getId()
        );

        if (answer != 1) {
            return false;
        }
        return true;
    }

    public int getFilmsCount() {
        String sqlString = "SELECT Count(film_id) FROM films";
        Integer count;

        count = jdbcTemplate.queryForObject(sqlString, Integer.class);

        if (count == null) {
            return 0;
        }
        return count;
    }


    public Optional<Film> getFilmById(long filmId) {
        String queryFilm = "SELECT film_id, name, description, release_date, duration, mpa_rate FROM films WHERE film_id=?";


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
        String queryLikes = "SELECT user_id FROM films_likes WHERE film_id=?";

        return jdbcTemplate.queryForList(queryLikes, Long.class, filmId);
    }

    public Optional<MpaRate> getMpaRateById(int mpaId) {
        String queryMpaRate = "SELECT mpa_id, name FROM mpa_rate WHERE MPA_ID=?";

        List<MpaRate> result = jdbcTemplate.query(queryMpaRate, mpaRateRowMapper, mpaId);

        return result.stream().findFirst();

    }

    public Collection<MpaRate> getMpaRates() {
        String queryMpaRates = "SELECT mpa_id, name FROM mpa_rate";
        return jdbcTemplate.query(queryMpaRates, mpaRateRowMapper);
    }

    public Optional<Genre> getGenreById(int genreId) {
        String queryGenre = "SELECT genre_id, name FROM genres WHERE genre_id=?";
        List<Genre> result = jdbcTemplate.query(queryGenre, genreRowMapper, genreId);

        return result.stream().findFirst();
    }

    public Collection<Genre> getGenres() {
        String queryGenre = "SELECT genre_id, name FROM genres";

        return jdbcTemplate.query(queryGenre, genreRowMapper);
    }


    public void addFilmGenres(Long filmId, List<Genre> genres) {
        String sqlString = "INSERT INTO films_genres(film_id, genre_id) " +
                "values (?, ?)";

        genres.stream()
                .distinct()  // добавляем только уникальные жанры
                .forEach(genre -> {
                    if (!isGenreIndexOK(genre.getId())) {
                        throw new NotFoundException("genre_id index = " + genre.getId() + " not found");
                    }
                    jdbcTemplate.update(sqlString, filmId, genre.getId());

                });
    }

    public List<Genre> getFilmGenres(Long filmId) {
        String queryFilmGenres = "SELECT g.genre_id, g.name FROM films_genres fg " +
                "JOIN genres g ON fg.genre_id = g.genre_id " +
                "WHERE fg.film_id=?";

        return jdbcTemplate.query(queryFilmGenres, genreRowMapper, filmId);
    }

    public int addFilmUserLikes(Long filmId, Long userId) {
        String sqlString = "INSERT INTO films_likes(film_id, user_id) " +
                "values (?, ?)";

        return jdbcTemplate.update(sqlString, filmId, userId);
    }

    public List<Long> getFilmUserLikes(long filmId) {
        String queryFilmUserLikes = "SELECT user_id FROM films_likes WHERE film_id=?";

        return jdbcTemplate.queryForList(queryFilmUserLikes, Long.class, filmId);
    }

    public Collection<Film> getAllFilms() {
        String sqlString = "SELECT film_id, name, description, release_date, duration, mpa_rate FROM films";

        return jdbcTemplate.query(sqlString, filmRowMapper);
    }

    private boolean isMpaRateIndexNotOK(int mpaRateIndex) {
        String sqlString = "SELECT Count(MPA_ID) FROM mpa_rate";
        Integer count;


        count = jdbcTemplate.queryForObject(sqlString, Integer.class);

        if (count == null) {
            return false;
        }

        return mpaRateIndex > count;
    }

    private boolean isGenreIndexOK(int genreIndex) {
        String sqlString = "SELECT COUNT(genre_id) FROM genres";
        Integer count;

        count = jdbcTemplate.queryForObject(sqlString, Integer.class);

        if (count == null) {
            return false;
        }

        return !(genreIndex > count);

    }

    public Collection<Film> getTopFilms(int count) {
        String getTopSql = "SELECT f.* FROM films f " +
                "JOIN films_likes fl ON f.film_id = fl.film_id " +
                "GROUP BY f.film_id " +
                "ORDER BY COUNT(user_id) DESC " +
                "LIMIT ?";
        return jdbcTemplate.query(getTopSql, filmRowMapper, count);
    }

    public boolean deleteUserLike(long filmId, long userId) {
        String deleteUserLikeSql = "DELETE FROM films_likes " +
                "WHERE film_id=? AND user_id=?";


        return jdbcTemplate.update(deleteUserLikeSql, filmId, userId) > 0;
    }

    public Map<Long, List<Genre>> getFilmGenresByFilmIds(Collection<Long> filmIds) {
        if (filmIds.isEmpty()) {
            return Collections.emptyMap();
        }

        String inClause = String.join(",", Collections.nCopies(filmIds.size(), "?"));
        String query = "SELECT fg.film_id, g.genre_id, g.name " +
                "FROM films_genres fg " +
                "JOIN genres g ON fg.genre_id = g.genre_id " +
                "WHERE fg.film_id IN (" + inClause + ") " +
                "ORDER BY fg.film_id, g.genre_id";

        Map<Long, List<Genre>> result = new HashMap<>();

        jdbcTemplate.query(query, filmIds.toArray(), rs -> {
            Long filmId = rs.getLong("film_id");
            Genre genre = new Genre(
                    rs.getInt("genre_id"),
                    rs.getString("name")
            );

            result.computeIfAbsent(filmId, k -> new ArrayList<>()).add(genre);
        });

        return result;
    }

    public Map<Long, Set<Long>> getFilmLikesByFilmIds(Collection<Long> filmIds) {
        if (filmIds.isEmpty()) {
            return Collections.emptyMap();
        }

        String inClause = String.join(",", Collections.nCopies(filmIds.size(), "?"));
        String query = "SELECT film_id, user_id FROM films_likes " +
                "WHERE film_id IN (" + inClause + ")";

        Map<Long, Set<Long>> result = new HashMap<>();

        jdbcTemplate.query(query, filmIds.toArray(), rs -> {
            Long filmId = rs.getLong("film_id");
            Long userId = rs.getLong("user_id");

            result.computeIfAbsent(filmId, k -> new HashSet<>()).add(userId);
        });

        return result;
    }
}