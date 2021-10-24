package com.teamone.typinggame.exceptions;

public class ActiveUserException extends Exception {
    public ActiveUserException(String errorMessage) {
        super(errorMessage);
    }
}
