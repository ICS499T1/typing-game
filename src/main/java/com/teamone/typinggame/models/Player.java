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

    private List<Character> failedCharacters;

    private Stack<Character> incorrectCharacters;

    private String gameId;

    private Boolean winner;

    private Long endTime;

    public Player(User user, String gameId) {
        this.userID = user.getUserID();
        this.gameId = gameId;
        this.position = 0;
        this.incorrectCharacters = new Stack<>();
        this.failedCharacters = new ArrayList<>();
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

    public void addFailedCharacter(Character character) {
        failedCharacters.add(character);
    }

    public void addIncorrectCharacter(Character character) {
        incorrectCharacters.add(character);
    }

    public void removeIncorrectCharacter() {
        incorrectCharacters.pop();
    }

    public void reset() {
        position = 0;
        failedCharacters = new ArrayList<>();
        incorrectCharacters = new Stack<>();
        winner = false;
        endTime = 0L;
    }
}
