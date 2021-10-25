package com.teamone.typinggame.models;

import com.teamone.typinggame.configuration.GameConfig;
import lombok.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

import static com.teamone.typinggame.models.GameStatus.*;

@NoArgsConstructor
@Component
public class Game {

    @Autowired
    private GameConfig gameConfig;

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
    @Setter
    private Integer doneCount;

    @Getter
    private Integer playerCount;

    public Game(String gameId) {
        this.gameId = gameId;
        this.players = new HashMap<>();
        this.playerCount = 0;
        this.status = WAITING_FOR_ANOTHER_PLAYER;
    }

    public boolean addPlayer(String sessionId, Player player) {
        if (playerCount <= 4 ) {
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
        players.forEach((sessionId, player) -> player.reset());
        startTime = System.currentTimeMillis();
    }

    public void updatePlayer(String sessionId, Player player) {
        players.put(sessionId, player);
    }

    public void incrementDoneCount() {
        doneCount++;
    }
}
