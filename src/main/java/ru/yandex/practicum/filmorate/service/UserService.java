package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dto.user.NewUserRequestDTO;
import ru.yandex.practicum.filmorate.dto.user.UpdateUserRequestDTO;
import ru.yandex.practicum.filmorate.dto.user.UserResponseDTO;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.StorageException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.mapper.user.UserMapper;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.Collection;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
public class UserService {


    private final UserStorage userStorage;
    private final UserMapper userMapper;


    public UserService(@Qualifier("userDbStorage") UserStorage userStorage, UserMapper userMapper) {
        this.userStorage = userStorage;
        this.userMapper = userMapper;
    }

    public UserResponseDTO createUser(NewUserRequestDTO newUserRequestDTO) {

        User newUser = userMapper.toUser(newUserRequestDTO);

        setUserNameIfEmpty(newUser);
        Long newUserId = userStorage.createUser(newUser);
        if (newUserId == 0) {
            throw new StorageException(newUser + " wasn't created");
        }
        Optional<User> userInStorage = userStorage.findUserById(newUserId);
        if (userInStorage.isEmpty()) {
            throw new StorageException(newUser + " wasn't created");
        }
        log.info("New user with id={} was created", userInStorage.get().getId());

        return userMapper.toUserResponseDTO(userInStorage.get());
    }

    public Collection<UserResponseDTO> getUsers() {
        return userMapper.toUserResponseDTO(userStorage.getUsers());
    }

    public UserResponseDTO getUserById(long id) {
        return userMapper.toUserResponseDTO(checkAndGetUserById(id));
    }

    public UserResponseDTO updateUser(UpdateUserRequestDTO userToUpdateDTO) {

        User userToUpdate = userMapper.toUser(userToUpdateDTO);

        validateUserToUpdate(userToUpdate);
        boolean wasUpdated = userStorage.updateUser(userToUpdate);
        if (!wasUpdated) {
            throw new StorageException(userToUpdate + " wasn't updated");
        }
        log.info("User with id={} was updated", userToUpdate.getId());
        Optional<User> userOptional = userStorage.findUserById(userToUpdate.getId());


        return userMapper.toUserResponseDTO(userOptional.get());
    }

    public void addFriend(long userId, long friendId) {
        checkAndGetUserById(userId);
        checkAndGetUserById(friendId);

        if (userId == friendId) {
            throw new ValidationException("User can't be a friend to himself");
        }

        if (isUserHaveFriend(userId, friendId)) {
            throw new ValidationException("User already have a friend with id=" + friendId);
        }

        boolean friendWasAddedToUser = userStorage.addFriend(userId, friendId);
        if (!friendWasAddedToUser) {
            throw new StorageException("Friend with id=" + friendId + " wasn't added to User with id=" + userId);
        }


    }

    public void deleteFriend(long userId, long friendId) {
        checkAndGetUserById(userId);
        checkAndGetUserById(friendId);
        if (userId == friendId) {
            throw new ValidationException("User can't be a friend to himself");
        }

        if (!isUserHaveFriend(userId, friendId)) {
            return;
        }

        boolean friendWasDeletedFromUser = userStorage.deleteFriend(userId, friendId);
        if (!friendWasDeletedFromUser) {
            throw new StorageException("Friend with id=" + friendId + " wasn't deleted from User with id=" + userId);
        }

        if (isUserHaveFriend(friendId, userId)) {
            boolean userWasDeletedFromExFriendUser = userStorage.deleteFriend(friendId, userId);
            if (!userWasDeletedFromExFriendUser) {
                throw new StorageException("Friend with id=" + friendId + " wasn't deleted from User with id=" + userId);
            }
        }
    }

    private void setUserNameIfEmpty(User user) {
        if (user.getName() == null || user.getName().isEmpty()) {
            user.setName(user.getLogin());
        }
    }

    private void validateUserToUpdate(User user) {
        setUserNameIfEmpty(user);
        checkAndGetUserById(user.getId());
    }

    protected User checkAndGetUserById(long id) {
        Optional<User> mayBeUser = userStorage.findUserById(id);
        if (mayBeUser.isEmpty()) {
            String message = "User with id=" + id + " not found";
            log.warn(message);
            throw new NotFoundException(message);
        }
        return mayBeUser.get();
    }

    protected boolean isUserHaveFriend(Long user_id, Long friend_id) {

        return !userStorage.getUserFriends(user_id).stream()
                .filter(user -> Objects.equals(user.getId(), friend_id))
                .toList().isEmpty();
    }

    public Collection<UserResponseDTO> getUserFriends(long userId) {
        checkAndGetUserById(userId);
        return userMapper.toUserResponseDTO(userStorage.getUserFriends(userId));
    }

    public Collection<UserResponseDTO> getUserCommonFriends(long userId, long otherUserId) {

        if (userId == otherUserId) {
            throw new ValidationException("User can't have common friends with himself");
        }

        checkAndGetUserById(userId);
        checkAndGetUserById(otherUserId);

        Set<User> userFriends = userStorage.getUserFriends(userId);
        Set<User> otherUserIdFriends = userStorage.getUserFriends(otherUserId);

        return userMapper.toUserResponseDTO(userFriends
                .stream()
                .filter(otherUserIdFriends::contains)
                .collect(Collectors.toSet()));
    }
}