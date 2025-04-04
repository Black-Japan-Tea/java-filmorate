package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.*;
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
            user.getFriends().add(friendId);
            log.debug("Текущий список друзей пользователя {}: {}", userId, user.getFriends());
        } catch (RuntimeException e) {
            throw new UserNotFoundException("User not found");
        }
        try {
            User friend = userStorage.getUserById(friendId);
            friend.getFriends().add(userId);
            log.debug("Текущий список друзей пользователя {}: {}", friendId, friend.getFriends());
        } catch (RuntimeException e) {
            throw new UserNotFoundException("Friend not found");
        }
    }

    public void removeFriend(Long userId, Long friendId) {
        // Получаем пользователя и проверяем его существование
        Optional<User> userOptional = Optional.ofNullable(userStorage.getUserById(userId));
        if (userOptional.isEmpty()) {
            throw new UserNotFoundException("User with id " + userId + " not found");
        }
        User user = userOptional.get();

        // Получаем друга и проверяем его существование
        Optional<User> friendOptional = Optional.ofNullable(userStorage.getUserById(friendId));
        if (friendOptional.isEmpty()) {
            throw new UserNotFoundException("Friend with id " + friendId + " not found");
        }
        User friend = friendOptional.get();

        // Удаляем дружбу в обе стороны (не проверяем, существовала ли она)
        user.getFriends().remove(friendId);
        friend.getFriends().remove(userId);
    }

    public List<User> getFriends(Long userId) {
        // Получаем пользователя и проверяем его существование
        Optional<User> userOptional = Optional.ofNullable(userStorage.getUserById(userId));
        if (userOptional.isEmpty()) {
            throw new UserNotFoundException("User with id " + userId + " not found");
        }
        User user = userOptional.get();

        List<User> friends = new ArrayList<>();
        for (Long friendId : user.getFriends()) {
            // Для каждого друга проверяем его существование
            Optional<User> friendOptional = Optional.ofNullable(userStorage.getUserById(friendId));
            if (friendOptional.isEmpty()) {
                throw new UserNotFoundException("Friend with id " + friendId + " not found");
            }
            friends.add(friendOptional.get());
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