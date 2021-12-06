package com.teamone.typinggame.exceptions;

public class GameNotFoundException extends Exception {
    public GameNotFoundException (String errorMessage) {
        super(errorMessage);
    }
}
