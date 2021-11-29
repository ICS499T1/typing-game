package com.teamone.typinggame.exceptions;

public class InvalidUsernameException extends Exception {
    public InvalidUsernameException(String errorMessage) {
        super(errorMessage);
    }
}
