package com.teamone.typinggame.models;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Data
@Component
@NoArgsConstructor
public class Player {
    private Long userID;

    private Integer position;

    private List<Character> incorrectCharacters;

    private String gameId;

    public Player(User user, String gameId) {
        this.userID = user.getUserID();
        this.gameId = gameId;
        this.position = 0;
        this.incorrectCharacters = new ArrayList<>();
    }

    @Override
    public boolean equals(Object o) {
        return (o instanceof Player && ((Player) o).getUserID().equals(this.userID));
    }

    @Override
    public int hashCode() {
        return userID.hashCode();
    }

    public void incrementPosition() {
        position++;
    }
}
