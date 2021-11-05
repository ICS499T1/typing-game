package com.teamone.typinggame.exceptions;

public class UserAlreadyExistsException extends Exception {
    public UserAlreadyExistsException (String errorMessage) {
        super(errorMessage);
    }
}
