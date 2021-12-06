package com.teamone.typinggame.exceptions;

public class InvalidGameStateException extends Exception {
    public InvalidGameStateException (String errorMessage) {
        super(errorMessage);
    }
}
