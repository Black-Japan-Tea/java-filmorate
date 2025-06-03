package ru.yandex.practicum.filmorate.controller.film;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.dto.user.NewUserRequestDTO;
import ru.yandex.practicum.filmorate.dto.user.UpdateUserRequestDTO;
import ru.yandex.practicum.filmorate.dto.user.UserResponseDTO;
import ru.yandex.practicum.filmorate.service.UserService;

import java.util.Collection;

@RestController
@RequestMapping("/users")
@AllArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping
    public UserResponseDTO createUser(@RequestBody @Valid NewUserRequestDTO newUser) {
        return userService.createUser(newUser);
    }

    @GetMapping
    public Collection<UserResponseDTO> getUsers() {
        return userService.getUsers();
    }

    @GetMapping("/{userId}")
    public UserResponseDTO getUserById(@PathVariable long userId) {
        return userService.getUserById(userId);
    }

    @GetMapping("/{userId}/friends")
    public Collection<UserResponseDTO> getUserFriends(@PathVariable long userId) {
        return userService.getUserFriends(userId);
    }

    @GetMapping("/{userId}/friends/common/{otherUserId}")
    public Collection<UserResponseDTO> getUserCommonFriends(@PathVariable long userId, @PathVariable long otherUserId) {
        return userService.getUserCommonFriends(userId, otherUserId);
    }

    @PutMapping
    public UserResponseDTO updateUser(@RequestBody @Valid UpdateUserRequestDTO userToUpdate) {
        return userService.updateUser(userToUpdate);
    }

    @PutMapping("/{userId}/friends/{friendId}")
    public void addFriend(@PathVariable long userId, @PathVariable long friendId) {
        userService.addFriend(userId, friendId);
    }

    @DeleteMapping("/{userId}/friends/{friendId}")
    public void deleteFriend(@PathVariable long userId, @PathVariable long friendId) {
        userService.deleteFriend(userId, friendId);
    }
}