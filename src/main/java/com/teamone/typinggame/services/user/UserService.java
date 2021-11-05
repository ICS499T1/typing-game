package com.teamone.typinggame.services.user;

import com.teamone.typinggame.exceptions.UserAlreadyExistsException;
import com.teamone.typinggame.models.User;

import java.util.List;

public interface UserService {
    User newUser(User user) throws UserAlreadyExistsException;
    List<User> getUsers();
}
