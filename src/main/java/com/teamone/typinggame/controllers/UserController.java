package com.teamone.typinggame.controllers;

import com.teamone.typinggame.exceptions.InvalidSignUpInfoException;
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

    /**
     * Creates a new user.
     *
     * @param signUpInfo - sign up object with username and password information
     * @return User - newly created user
     * @throws UserAlreadyExistsException when the username exists
     */
    @PostMapping("/add")
    public User newUser(@RequestBody SignUp signUpInfo) throws UserAlreadyExistsException, InvalidSignUpInfoException {
        User user = new User();
        user.setUsername(signUpInfo.getUsername());
        user.setPassword(signUpInfo.getPassword());
        return userServiceImpl.newUser(user);
    }

    /**
     * Returns user information based on the username. Will work only if the user
     * is authorized.
     *
     * @param username - username
     * @return UserDetails - user info
     */
    @PostAuthorize("returnObject.username == authentication.name")
    @PostMapping("/getuser")
    public UserDetails getUser(@RequestParam("username") String username) {
        return userServiceImpl.loadUserByUsername(username);
    }

    /**
     * Refreshes access token for the user.
     * @param request - user request
     * @param response - server response
     * @throws IOException when an input or output error occurs
     */
    @GetMapping("/refresh")
    public void refreshToken(HttpServletRequest request, HttpServletResponse response) throws IOException {
        authToken.refreshToken(request, response);
    }
}
