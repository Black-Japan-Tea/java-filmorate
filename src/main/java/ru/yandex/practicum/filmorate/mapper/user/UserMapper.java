package ru.yandex.practicum.filmorate.mapper.user;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dto.user.NewUserRequestDTO;
import ru.yandex.practicum.filmorate.dto.user.UpdateUserRequestDTO;
import ru.yandex.practicum.filmorate.dto.user.UserResponseDTO;
import ru.yandex.practicum.filmorate.model.User;

import java.util.ArrayList;
import java.util.Collection;
import java.util.stream.Collectors;

@Component
public class UserMapper {
    public UserResponseDTO toUserResponseDTO(User user) {
        return UserResponseDTO.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .login(user.getLogin())
                .birthday(user.getBirthday())
                .friends(user.getFriends())
                .build();
    }

    public Collection<UserResponseDTO> toUserResponseDTO(Collection<User> users) {
        return users.stream()
                .map(user -> UserResponseDTO.builder()
                        .id(user.getId())
                        .name(user.getName())
                        .email(user.getEmail())
                        .login(user.getLogin())
                        .birthday(user.getBirthday())
                        .friends(user.getFriends())
                        .build())
                .collect(Collectors.toCollection(ArrayList::new));

    }

    public User toUser(NewUserRequestDTO newUserRequestDTO) {
        return User.builder()
                .email(newUserRequestDTO.getEmail())
                .login(newUserRequestDTO.getLogin())
                .name(newUserRequestDTO.getName())
                .birthday(newUserRequestDTO.getBirthday())
                .build();
    }

    public User toUser(UpdateUserRequestDTO updateUserRequestDTO) {
        return User.builder()
                .id(updateUserRequestDTO.getId())
                .email(updateUserRequestDTO.getEmail())
                .login(updateUserRequestDTO.getLogin())
                .name(updateUserRequestDTO.getName())
                .birthday(updateUserRequestDTO.getBirthday())
                .build();
    }
}