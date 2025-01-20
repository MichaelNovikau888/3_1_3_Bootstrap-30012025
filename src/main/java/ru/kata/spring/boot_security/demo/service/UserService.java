package ru.kata.spring.boot_security.demo.service;

import ru.kata.spring.boot_security.demo.model.User;

import java.util.List;
import java.util.Optional;

public interface UserService {
    List<User> getAllUsers();

    Optional<User> getUserById(Long id);

    User getUserByUsername(String username);

    User saveUser(User user);
    User updateUser(User user);
    void deleteUser(Long id);
}