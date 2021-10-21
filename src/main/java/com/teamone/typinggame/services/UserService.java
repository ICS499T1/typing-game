package com.teamone.typinggame.services;

import com.teamone.typinggame.models.User;

import java.util.List;

public interface UserService {
    User newUser(User user);
    List<User> getUsers();
}
