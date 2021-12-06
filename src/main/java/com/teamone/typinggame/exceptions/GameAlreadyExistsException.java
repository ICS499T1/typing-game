package com.teamone.typinggame.exceptions;

public class GameAlreadyExistsException extends Exception {
    public GameAlreadyExistsException (String errorMessage) {
        super(errorMessage);
    }
}