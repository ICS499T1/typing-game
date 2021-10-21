package com.teamone.typinggame.controllers;

import com.teamone.typinggame.models.User;
import com.teamone.typinggame.services.UserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user")
public class UserController {

    private final UserServiceImpl userServiceImpl;

    @Autowired
    public UserController(UserServiceImpl userServiceImpl) {
        this.userServiceImpl = userServiceImpl;
    }

    @PostMapping("/add")
    public User newUser(@RequestBody User user) {
        return userServiceImpl.newUser(user);
    }

    @GetMapping("/getusers")
    public List<User> getUsers() {
        return userServiceImpl.getUsers();
    }
}
