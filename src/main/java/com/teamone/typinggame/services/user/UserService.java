package com.teamone.typinggame.services.user;

import com.teamone.typinggame.exceptions.UserAlreadyExistsException;
import com.teamone.typinggame.exceptions.UserNotFoundException;
import com.teamone.typinggame.models.User;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;

public interface UserService {
    User newUser(User user) throws UserAlreadyExistsException;
    UserDetails authorizeUser(User user) throws UserNotFoundException;
}
