package com.teamone.typinggame.models;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

@Data
@Component
@NoArgsConstructor
public class Player {
    private Long userID;

    private Integer position;

    private List<Character> permanentIncorrectCharacters;

    private Stack<Character> tempIncorrectCharacters;

    private String gameId;

    private Boolean winner;

    private Long endTime;

    public Player(User user, String gameId) {
        this.userID = user.getUserID();
        this.gameId = gameId;
        this.position = 0;
        this.tempIncorrectCharacters = new Stack<>();
        this.permanentIncorrectCharacters = new ArrayList<>();
    }

//    @Override
//    public boolean equals(Object o) {
//        return (o instanceof Player && ((Player) o).getUserID().equals(this.userID));
//    }

//    @Override
//    public int hashCode() {
//        return userID.hashCode();
//    }

    public void incrementPosition() {
        position++;
    }

    public void addIncorrectCharacter(Character character) {
        permanentIncorrectCharacters.add(character);
        tempIncorrectCharacters.add(character);
    }

    public void removeCharacter() {
        tempIncorrectCharacters.pop();
    }

    public void reset() {
        position = 0;
        permanentIncorrectCharacters = new ArrayList<>();
        tempIncorrectCharacters = new Stack<>();
        winner = false;
        endTime = 0L;
    }
}
