package com.teamone.typinggame.exceptions;

public class PlayerNotFoundException extends Exception {
    public PlayerNotFoundException (String errorMessage) {
        super(errorMessage);
    }
}