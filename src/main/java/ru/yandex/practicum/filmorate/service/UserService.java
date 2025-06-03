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
        log.debug("UserService initialized with UserStorage: {} and UserMapper: {}",
                userStorage.getClass(), userMapper.getClass());
    }

    public UserResponseDTO createUser(NewUserRequestDTO newUserRequestDTO) {
        log.info("Creating new user from DTO: {}", newUserRequestDTO);
        User newUser = userMapper.toUser(newUserRequestDTO);

        setUserNameIfEmpty(newUser);
        Long newUserId = userStorage.createUser(newUser);

        try {
            User createdUser = checkAndGetUserById(newUserId);
            log.info("Successfully created user with ID: {}", createdUser.getId());
            log.debug("Created user details: {}", createdUser);
            return userMapper.toUserResponseDTO(createdUser);
        } catch (NotFoundException ex) {
            String errorMessage = "User with id=" + newUserId + " wasn't created";
            log.error(errorMessage);
            throw new StorageException(ex.getMessage());
        }
    }

    public Collection<UserResponseDTO> getUsers() {
        log.info("Getting all users");
        Collection<User> users = userStorage.getUsers();
        log.debug("Retrieved {} users", users.size());
        return userMapper.toUserResponseDTO(users);
    }

    public UserResponseDTO getUserById(long id) {
        log.info("Getting user by ID: {}", id);
        User user = checkAndGetUserById(id);
        log.debug("Found user: {}", user);
        return userMapper.toUserResponseDTO(user);
    }

    public UserResponseDTO updateUser(UpdateUserRequestDTO userToUpdateDTO) {
        log.info("Updating user with DTO: {}", userToUpdateDTO);
        User userToUpdate = userMapper.toUser(userToUpdateDTO);

        validateUserToUpdate(userToUpdate);
        boolean wasUpdated = userStorage.updateUser(userToUpdate);
        if (!wasUpdated) {
            String errorMessage = userToUpdate + " wasn't updated";
            log.error(errorMessage);
            throw new StorageException(errorMessage);
        }

        Optional<User> userOptional = userStorage.findUserById(userToUpdate.getId());
        User updatedUser = userOptional.get();
        log.info("Successfully updated user with ID: {}", updatedUser.getId());
        log.debug("Updated user details: {}", updatedUser);

        return userMapper.toUserResponseDTO(updatedUser);
    }

    public void addFriend(long userId, long friendId) {
        log.info("Adding friend {} to user {}", friendId, userId);

        if (userId == friendId) {
            String errorMessage = "User can't be a friend to himself";
            log.error(errorMessage);
            throw new ValidationException(errorMessage);
        }

        User user = checkAndGetUserById(userId);
        checkAndGetUserById(friendId);
        Set<User> userFriends = userStorage.getUserFriends(userId);

        if (isUserHaveFriend(userFriends, friendId)) {
            String errorMessage = "User already have a friend with id=" + friendId;
            log.warn(errorMessage);
            throw new ValidationException(errorMessage);
        }

        boolean friendWasAddedToUser = userStorage.addFriend(userId, friendId);
        if (!friendWasAddedToUser) {
            String errorMessage = "Friend with id=" + friendId + " wasn't added to User with id=" + userId;
            log.error(errorMessage);
            throw new StorageException(errorMessage);
        }
        log.info("Successfully added friend {} to user {}", friendId, userId);
    }

    public void deleteFriend(long userId, long friendId) {
        log.info("Removing friend {} from user {}", friendId, userId);
        User user = checkAndGetUserById(userId);
        User friend = checkAndGetUserById(friendId);
        Set<User> userFriends = userStorage.getUserFriends(userId);

        if (userId == friendId) {
            String errorMessage = "User can't be a friend to himself";
            log.error(errorMessage);
            throw new ValidationException(errorMessage);
        }

        if (!isUserHaveFriend(userFriends, friendId)) {
            log.warn("User {} doesn't have friend {}, nothing to delete", userId, friendId);
            return;
        }

        boolean friendWasDeletedFromUser = userStorage.deleteFriend(userId, friendId);
        if (!friendWasDeletedFromUser) {
            String errorMessage = "Friend with id=" + friendId + " wasn't deleted from User with id=" + userId;
            log.error(errorMessage);
            throw new StorageException(errorMessage);
        }

        Set<User> friendFriends = userStorage.getUserFriends(friendId);
        if (isUserHaveFriend(friendFriends, userId)) {
            boolean userWasDeletedFromExFriendUser = userStorage.deleteFriend(friendId, userId);
            if (!userWasDeletedFromExFriendUser) {
                String errorMessage = "Friend with id=" + friendId + " wasn't deleted from User with id=" + userId;
                log.error(errorMessage);
                throw new StorageException(errorMessage);
            }
        }
        log.info("Successfully removed friend {} from user {}", friendId, userId);
    }

    private void setUserNameIfEmpty(User user) {
        if (user.getName() == null || user.getName().isEmpty()) {
            log.debug("Setting login as name for user with login: {}", user.getLogin());
            user.setName(user.getLogin());
        }
    }

    private void validateUserToUpdate(User user) {
        log.debug("Validating user for update: {}", user);
        setUserNameIfEmpty(user);
        checkAndGetUserById(user.getId());
    }

    protected User checkAndGetUserById(long id) {
        log.debug("Checking existence of user with ID: {}", id);
        Optional<User> mayBeUser = userStorage.findUserById(id);
        if (mayBeUser.isEmpty()) {
            String message = "User with id=" + id + " not found";
            log.error(message);
            throw new NotFoundException(message);
        }
        return mayBeUser.get();
    }

    protected boolean isUserHaveFriend(Set<User> userFriends, long friendId) {
        log.debug("Checking if user has friend {}", friendId);
        return userFriends.stream()
                .anyMatch(user -> user.getId() == friendId);
    }

    public Collection<UserResponseDTO> getUserFriends(long userId) {
        log.info("Getting friends for user {}", userId);
        checkAndGetUserById(userId);
        Collection<User> friends = userStorage.getUserFriends(userId);
        log.debug("Retrieved {} friends for user {}", friends.size(), userId);
        return userMapper.toUserResponseDTO(friends);
    }

    public Collection<UserResponseDTO> getUserCommonFriends(long userId, long otherUserId) {
        log.info("Getting common friends between user {} and user {}", userId, otherUserId);

        if (userId == otherUserId) {
            String errorMessage = "User can't have common friends with himself";
            log.error(errorMessage);
            throw new ValidationException(errorMessage);
        }

        checkAndGetUserById(userId);
        checkAndGetUserById(otherUserId);

        Set<User> userFriends = userStorage.getUserFriends(userId);
        Set<User> otherUserIdFriends = userStorage.getUserFriends(otherUserId);

        Collection<User> commonFriends = userFriends.stream()
                .filter(otherUserIdFriends::contains)
                .collect(Collectors.toSet());

        log.debug("Found {} common friends between user {} and user {}",
                commonFriends.size(), userId, otherUserId);

        return userMapper.toUserResponseDTO(commonFriends);
    }
}