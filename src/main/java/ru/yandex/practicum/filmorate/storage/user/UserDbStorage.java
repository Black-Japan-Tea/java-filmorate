package ru.yandex.practicum.filmorate.storage.user;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dal.UserRepository;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@Qualifier("userDbStorage")
@RequiredArgsConstructor
public class UserDbStorage implements UserStorage {

    private final UserRepository userRepository;

    @Override
    public Long createUser(User newUser) {
        return userRepository.createUser(newUser);
    }

    @Override
    public Collection<User> getUsers() {
        Collection<User> users = userRepository.getAllUsers();

        users.forEach(this::addUserFriends);

        return users;
    }

    @Override
    public boolean updateUser(User userToUpdate) {
        return userRepository.updateUser(userToUpdate);
    }

    @Override
    public Optional<User> findUserById(long id) {
        Optional<User> optionalUser = userRepository.getUserById(id);

        optionalUser.ifPresent(this::addUserFriends);

        return optionalUser;
    }

    @Override
    public boolean addFriend(long userId, long friendId) {
        return userRepository.addFriend(userId, friendId);
    }

    @Override
    public boolean deleteFriend(long userId, long friendId) {
        return userRepository.deleteFriend(userId, friendId);
    }

    @Override
    public Set<User> getUserFriends(long id) {
        return new HashSet<>(userRepository.getUserFriends(id));
    }

    private void addUserFriends(User user) {
        user.setFriends(userRepository.getUserFriends(
                        user.getId()).stream()
                .map(User::getId)
                .collect(Collectors.toSet()));
    }
}