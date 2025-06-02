package ru.yandex.practicum.filmorate.dal;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Slf4j
@Repository
@RequiredArgsConstructor
public class UserRepository {
    protected final JdbcTemplate jdbcTemplate;
    private final RowMapper<User> userRowMapper;

    public Optional<User> getUserById(long userId) {

        String sqlString = "SELECT USER_ID, NAME, EMAIL, LOGIN, BIRTHDAY FROM USERS WHERE user_id=?";

        Optional<User> user;
        try {
            user = Optional.ofNullable(jdbcTemplate.queryForObject(sqlString, userRowMapper, userId));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
        return user;
    }


    public Collection<User> getAllUsers() {
        String sqlString = "SELECT USER_ID, NAME, EMAIL, LOGIN, BIRTHDAY FROM USERS";

        return jdbcTemplate.query(sqlString, userRowMapper);
    }

    public long createUser(User newUser) {

        String sqlString = "INSERT INTO users(NAME, EMAIL, LOGIN, BIRTHDAY) " +
                "values (?, ?, ?, ?)";


        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(sqlString, new String[]{"USER_ID"});
            stmt.setString(1, newUser.getName());
            stmt.setString(2, newUser.getEmail());
            stmt.setString(3, newUser.getLogin());
            stmt.setDate(4, Date.valueOf(newUser.getBirthday()));
            return stmt;
        }, keyHolder);


        return Objects.requireNonNull(keyHolder.getKey()).longValue();
    }

    public boolean addFriend(long userId, long friendId) {
        String sqlString = "INSERT INTO users_friends (user_id, friend_id) " +
                "values (?, ?)";
        int answer = jdbcTemplate.update(sqlString, userId, friendId);

        return answer == 1;
    }

    public List<User> getUserFriends(long userId) {

        String sqlString = "SELECT u.* FROM USERS u JOIN USERS_FRIENDS uf ON u.USER_ID = uf.FRIEND_ID WHERE uf.USER_ID=?";

        return jdbcTemplate.query(sqlString, userRowMapper, userId);
    }

    public boolean deleteFriend(long userId, long friendId) {
        String sqlString = "DELETE FROM USERS_FRIENDS WHERE USER_ID=? AND FRIEND_ID=?";

        return jdbcTemplate.update(sqlString, userId, friendId) > 0;
    }

    public boolean updateUser(User userToUpdate) {

        String sqlString = "UPDATE users SET NAME=?, EMAIL=?, LOGIN=?, BIRTHDAY=? WHERE USER_ID=" + userToUpdate.getId();

        int answer = jdbcTemplate.update(sqlString,
                userToUpdate.getName(),
                userToUpdate.getEmail(),
                userToUpdate.getLogin(),
                userToUpdate.getBirthday()
        );

        return answer == 1;
    }
}