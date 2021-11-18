package com.teamone.typinggame.controllers;

import com.teamone.typinggame.exceptions.UserAlreadyExistsException;
import com.teamone.typinggame.models.SignUp;
import com.teamone.typinggame.models.User;
import com.teamone.typinggame.services.user.UserServiceImpl;
import com.teamone.typinggame.utilities.AuthToken;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
@CrossOrigin
@RestController
@RequestMapping("/user")
public class UserController {

    private final AuthToken authToken;
    private final UserServiceImpl userServiceImpl;

    @Autowired
    public UserController(UserServiceImpl userServiceImpl, AuthToken authToken) {
        this.userServiceImpl = userServiceImpl;
        this.authToken = authToken;
    }

    @PostMapping("/add")
    public User newUser(@RequestBody SignUp signUpInfo) throws UserAlreadyExistsException {
        User user = new User();
        user.setUsername(signUpInfo.getUsername());
        user.setPassword(signUpInfo.getPassword());
        return userServiceImpl.newUser(user);
    }

    @PostAuthorize("returnObject.username == authentication.name")
    @PostMapping("/getuser")
    public UserDetails getUser(@RequestParam("username") String username) {
        return userServiceImpl.loadUserByUsername(username);
    }

    @GetMapping("/refresh")
    public void refreshToken(HttpServletRequest request, HttpServletResponse response) throws IOException {
        authToken.refreshToken(request, response);
    }

    @PostMapping("/info")
    public UserDetails userInfo(@RequestBody User user) {
        UserDetails loadedUser = userServiceImpl.authorizeUser(user);
        if (loadedUser != null) return loadedUser;
        else throw new IllegalStateException("Incorrect credentials.");
    }
}
