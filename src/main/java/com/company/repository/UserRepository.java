package com.company.repository;

import com.company.model.User;

import java.util.Optional;

public interface UserRepository {

    Optional<User> findByEmail(String email);

    void save(User user);

    Optional<User> findByUserName(String name);

    boolean isUsernameInUse(String name);
}
