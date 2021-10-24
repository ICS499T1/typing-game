package com.teamone.typinggame.models;

import lombok.*;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.teamone.typinggame.models.GameStatus.*;

@NoArgsConstructor
@Component
public class Game {
    @Getter
    private String gameId;

    @Getter
    @Setter
    private GameStatus status;

    @Getter
    @Setter
    private String gameText;

    @Getter
    private Map<String, Player> players;

    @Getter
    @Setter
    private Long startTime;

    @Getter
    @Setter
    private Player winner;

    @Getter
    private Integer playerCount;

    public Game(String gameId) {
        this.gameId = gameId;
        this.players = new HashMap<>();
        this.playerCount = 0;
        this.status = WAITING_FOR_ANOTHER_PLAYER;
    }

    public boolean addPlayer(String sessionId, Player player) {
        if (playerCount < 5 ) {
            playerCount++;
            players.put(sessionId, player);
            return true;
        }
        return false;
    }

    public boolean removePlayer(Player player) {
        Player removedPlayer = players.remove(player.getGameId());
        if (removedPlayer != null) {
            playerCount--;
            return true;
        }
        return false;
    }

    public Player getPlayer(String sessionId) {
        return players.get(sessionId);
    }

    public boolean containsPlayer(String sessionId) {
        return players.containsKey(sessionId);
    }

    public void reset() {
        status = IN_PROGRESS;
        gameText = "Random paragraph";
        winner = null;
        players.forEach((sessionId, player) -> {
            player.setPosition(0);
            player.setIncorrectCharacters(new ArrayList<>());
        });
        System.currentTimeMillis();
    }
}
