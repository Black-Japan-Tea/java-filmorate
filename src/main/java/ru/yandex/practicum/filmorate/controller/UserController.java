package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@RestController
@Slf4j
public class UserController {

    private final Map<Long, User> users = new HashMap<>();

    @GetMapping
    public Collection<User> getAllUsers() {
        log.info("Запрос на отображение всех пользователей ({}).", users);
        return users.values();
    }

    @PostMapping
    public User addUser(@Valid @RequestBody User user) {

        log.info("Запрос на добавление пользователя с id = {}", user.getId());

        if (user.getLogin().contains(" ")) {
            log.warn("Ошибка добавления нового пользователя: логин не может содержать пробелы.",
                    new ValidationException("Логин не может содержать пробелы."));
        }

        if (user.getName().isEmpty()) {
            user.setName(user.getLogin());
        }

        user.setId(getNextId());
        users.put(user.getId(), user);
        log.info("Пользователь с id = {} успешно добавлен", user.getId());

        return user;
    }

    @PutMapping
    public User updateUser(@Valid @RequestBody User newUser) {

        log.info("Запрос на обновление пользователя с id = {}", newUser.getId());

        if (newUser.getLogin().contains(" ")) {
            log.warn("Ошибка обновления пользователя: логин не может содержать пробелы.",
                    new ValidationException("Логин не может содержать пробелы."));
        }

        if (newUser.getName().isEmpty()) {
            newUser.setName(newUser.getLogin());
            log.info("Пользователь с id = {} не указал имя. В качестве имени будет использован его логин.", newUser.getId());
        }

        if (users.containsKey(newUser.getId())) {
            User oldUser = users.get(newUser.getId());

            if (!oldUser.getName().equals(newUser.getName())) {
                oldUser.setName(newUser.getName());
            }
            if (!oldUser.getBirthday().equals(newUser.getBirthday())) {
                oldUser.setBirthday(newUser.getBirthday());
            }
            if (!oldUser.getLogin().equals(newUser.getLogin())) {
                oldUser.setLogin(newUser.getLogin());
            }
            if (!oldUser.getEmail().equals(newUser.getEmail())) {
                oldUser.setEmail(newUser.getEmail());
            }
            log.info("Пользователь с id = {} успешно обновлён", newUser.getId());

            return oldUser;
        }

        log.warn("Ошибка обновления данных о пользователе. Пользователя с id = {} нет.", newUser.getId());
        throw new ValidationException("Пользователь с id = " + newUser.getId() + " не найден.");
    }

    private long getNextId() {
        long currentMaxId = users.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }
}