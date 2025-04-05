package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import java.util.Collection;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {
    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public Collection<User> getAllUsers() {
        log.info("Запрос на получение списка всех пользователей");
        Collection<User> users = userService.getAllUsers();
        log.info("Успешно возвращено {} пользователей", users.size());
        return users;
    }

    @GetMapping("/{id}")
    public User getUserById(@PathVariable Long id) {
        log.info("Запрос на получение пользователя с ID {}", id);
        User user = userService.getUserById(id);
        log.info("Найден пользователь: ID={}, Логин={}, Email={}",
                user.getId(), user.getLogin(), user.getEmail());
        return user;
    }

    @PostMapping
    public User createUser(@Valid @RequestBody User user) {
        log.info("Запрос на создание нового пользователя: Логин={}, Email={}",
                user.getLogin(), user.getEmail());
        User createdUser = userService.createUser(user);
        log.info("Пользователь успешно создан: ID={}, Логин={}",
                createdUser.getId(), createdUser.getLogin());
        return createdUser;
    }

    @PutMapping
    public User updateUser(@Valid @RequestBody User user) {
        log.info("Запрос на обновление пользователя с ID {}", user.getId());
        User updatedUser = userService.updateUser(user);
        log.info("Пользователь успешно обновлен: ID={}, Логин={}",
                updatedUser.getId(), updatedUser.getLogin());
        return updatedUser;
    }

    @PutMapping("/{id}/friends/{friendId}")
    public void addFriend(@PathVariable Long id, @PathVariable Long friendId) {
        log.info("Запрос на добавление в друзья: пользователь ID={} → пользователь ID={}",
                id, friendId);
        userService.addFriend(id, friendId);
        log.info("Дружба установлена: пользователь ID={} и пользователь ID={} теперь друзья",
                id, friendId);
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    public void removeFriend(@PathVariable Long id, @PathVariable Long friendId) {
        log.info("Запрос на удаление из друзей: пользователь ID={} → пользователь ID={}",
                id, friendId);
        userService.removeFriend(id, friendId);
        log.info("Дружба прекращена: пользователь ID={} и пользователь ID={} больше не друзья",
                id, friendId);
    }

    @GetMapping("/{id}/friends")
    public List<User> getFriends(@PathVariable Long id) {
        log.info("Запрос на получение списка друзей пользователя ID={}", id);
        List<User> friends = userService.getFriends(id);
        log.info("Найдено {} друзей для пользователя ID={}", friends.size(), id);
        return friends;
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    public List<User> getCommonFriends(@PathVariable Long id, @PathVariable Long otherId) {
        log.info("Запрос на поиск общих друзей: пользователь ID={} и пользователь ID={}",
                id, otherId);
        List<User> commonFriends = userService.getCommonFriends(id, otherId);
        log.info("Найдено {} общих друзей между пользователем ID={} и пользователем ID={}",
                commonFriends.size(), id, otherId);
        return commonFriends;
    }
}