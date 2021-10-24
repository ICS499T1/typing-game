package com.teamone.typinggame.exceptions;

public class UserNotFoundException extends Exception {
    public UserNotFoundException (String errorMessage) {
        super(errorMessage);
    }
}
