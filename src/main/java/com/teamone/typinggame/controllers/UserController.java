package com.teamone.typinggame.controllers;

import com.teamone.typinggame.models.User;
import com.teamone.typinggame.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/add")
    public User newUser(@RequestBody User user) {
        return userService.newUser(user);
    }

    @GetMapping("/getusers")
    public List<User> getUsers() {
        return userService.getUsers();
    }
}
