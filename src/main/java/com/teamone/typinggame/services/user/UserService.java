package com.teamone.typinggame.services.user;

import com.teamone.typinggame.exceptions.InvalidSignUpInfoException;
import com.teamone.typinggame.exceptions.UserAlreadyExistsException;
import com.teamone.typinggame.models.User;

public interface UserService {
    /**
     * Adds a newly created user to the database.
     *
     * @param user - user to be added
     * @return User - created user
     * @throws UserAlreadyExistsException when the user already exists
     */
    User newUser(User user) throws UserAlreadyExistsException, InvalidSignUpInfoException;

    /**
     * Updates user stats.
     *
     * @param user - user to be updated
     * @return User
     */
    User updateUserInfo(User user);
}
