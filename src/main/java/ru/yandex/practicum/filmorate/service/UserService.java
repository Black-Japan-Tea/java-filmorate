package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
public class UserService {
    private final UserStorage userStorage;

    @Autowired
    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public void addFriend(Long userId, Long friendId) {
        log.info("Добавление в друзья: пользователь {} добавляет пользователя {}", userId, friendId);
        try {
            User user = userStorage.getUserById(userId);
            User friend = userStorage.getUserById(friendId);

            user.getFriends().add(friendId);
            friend.getFriends().add(userId);
            log.debug("Текущий список друзей пользователя {}: {}", userId, user.getFriends());
            log.debug("Текущий список друзей пользователя {}: {}", friendId, friend.getFriends());
        } catch (Throwable e) {
            log.error("Ошибка при добавлении в друзья: {}", e.getMessage());
            throw e;
        }
    }

    public void removeFriend(Long userId, Long friendId) {
        User user = userStorage.getUserById(userId);
        User friend = userStorage.getUserById(friendId);

        user.getFriends().remove(friendId);
        friend.getFriends().remove(userId);
    }

    public List<User> getFriends(Long userId) {
        User user = userStorage.getUserById(userId);
        List<User> friends = new ArrayList<>();
        for (Long friendId : user.getFriends()) {
            friends.add(userStorage.getUserById(friendId));
        }
        return friends;
    }

    public List<User> getCommonFriends(Long userId, Long otherUserId) {
        log.info("Поиск общих друзей для пользователей {} и {}", userId, otherUserId);
        User user = userStorage.getUserById(userId);
        User otherUser = userStorage.getUserById(otherUserId);

        Set<Long> commonFriendIds = new HashSet<>(user.getFriends());
        commonFriendIds.retainAll(otherUser.getFriends());
        log.debug("Найдены общие друзья с ID: {}", commonFriendIds);

        return commonFriendIds.stream()
                .map(userStorage::getUserById)
                .collect(Collectors.toList());
    }
}