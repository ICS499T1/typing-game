package com.teamone.typinggame.controllers;

import com.teamone.typinggame.exceptions.UserAlreadyExistsException;
import com.teamone.typinggame.exceptions.UserNotFoundException;
import com.teamone.typinggame.models.User;
import com.teamone.typinggame.services.user.UserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("/user")
public class UserController {

    private final UserServiceImpl userServiceImpl;

    @Autowired
    public UserController(UserServiceImpl userServiceImpl) {
        this.userServiceImpl = userServiceImpl;
    }

    @PostMapping("/add")
    public User newUser(@RequestBody User user) throws UserAlreadyExistsException {
        return userServiceImpl.newUser(user);
    }

    @PostAuthorize("returnObject.username == authentication.name")
    @PostMapping("/getuser")
    public UserDetails getUser(@RequestParam("username") String username) {
        return userServiceImpl.loadUserByUsername(username);
    }

    @PostMapping("/info")
    public UserDetails userInfo(@RequestBody User user) {
        UserDetails loadedUser = userServiceImpl.authorizeUser(user);
        if (loadedUser != null) return loadedUser;
        else throw new IllegalStateException("Incorrect credentials.");
    }
}
