package com.teamone.typinggame.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.teamone.typinggame.configuration.GameConfig;
import lombok.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import static com.teamone.typinggame.models.GameStatus.*;

@RequiredArgsConstructor
@Component
@JsonIgnoreProperties({"gameText"})
public class Game {
    private final ReadWriteLock gameRwLock = new ReentrantReadWriteLock();
    private final ReadWriteLock playerSetLock = new ReentrantReadWriteLock();
    private final ReadWriteLock doneLock = new ReentrantReadWriteLock();

    @Getter
    private String gameId;

    private GameStatus status;

    @Getter
    @Setter
    private List<Character> gameText;

    @Getter
    private Map<String, Player> players;

    @Getter
    @Setter
    private Long startTime;

    private Player winner;

    private Integer doneCount;

    @Getter
    private Integer playerCount;

    private GameConfig gameConfig = new GameConfig();

    public Game(String gameId) {
        fillGameText();
        this.gameId = gameId;
        this.players = new HashMap<>();
        this.playerCount = 0;
        this.status = WAITING_FOR_ANOTHER_PLAYER;
        this.doneCount = 0;
    }

    public GameStatus getStatus() {
        gameRwLock.readLock().lock();
        try {
            return status;
        } finally {
            gameRwLock.readLock().unlock();
        }
    }

    public void setStatus(GameStatus status) {
        gameRwLock.writeLock().lock();
        try {
            this.status = status;
        } finally {
            gameRwLock.writeLock().unlock();
        }
    }

    public Integer getDoneCount() {
        doneLock.readLock().lock();
        try {
            return doneCount;
        } finally {
            doneLock.readLock().unlock();
        }
    }

    public Player getWinner() {
        doneLock.readLock().lock();
        try {
            return winner;
        } finally {
            doneLock.readLock().unlock();
        }
    }

    public void setWinner(Player player) {
        doneLock.writeLock().lock();
        try {
            winner = player;
            player.setWinner(true);
            doneCount++;
        } finally {
            doneLock.writeLock().unlock();
        }
    }

    public boolean addPlayer(String sessionId, Player player) {
        playerSetLock.writeLock().lock();
        try {
            if (playerCount <= 4) {
                playerCount++;
                players.put(sessionId, player);
                return true;
            }
            return false;
        } finally {
            playerSetLock.writeLock().unlock();
        }
    }

    public boolean removePlayer(Player player) {
        playerSetLock.readLock().lock();
        try {
            Player removedPlayer = players.remove(player.getGameId());
            if (removedPlayer != null) {
                playerCount--;
                return true;
            }
            return false;
        } finally {
            playerSetLock.readLock().unlock();
        }
    }

    public void fillGameText() {
        gameText = new ArrayList<>();
        String url = "http://metaphorpsum.com/paragraphs/1/10";
        RestTemplate restTemplate = new RestTemplate();
        String paragraph = restTemplate.getForObject(url, String.class);

        char[] characterArray = paragraph.toCharArray();
        for(char c : characterArray) {
            gameText.add(c);
        }

    }

    public Player getPlayer(String sessionId) {
        playerSetLock.readLock().lock();
        try {
            return players.get(sessionId);
        } finally {
            playerSetLock.readLock().unlock();
        }
    }

    public boolean containsPlayer(String sessionId) {
        playerSetLock.readLock().lock();
        try {
            return players.containsKey(sessionId);
        } finally {
            playerSetLock.readLock().unlock();
        }
    }

    private void resetPlayers() {
        playerSetLock.writeLock().lock();
        try {
            players.forEach((sessionId, player) -> player.reset());
        } finally {
            playerSetLock.writeLock().unlock();
        }
    }

    private void calculateStats() {
        playerSetLock.readLock().lock();
        try {
            //TODO remove this user and pass in a real user
            User user = new User();
            players.forEach((sessionId, player) -> player.calculateStats(startTime, gameText, user));
        } finally {
            playerSetLock.readLock().unlock();
        }
    }

    public void reset() {
        doneLock.writeLock().lock();
        gameRwLock.writeLock().lock();
        try {
            status = playerCount > 1 ? READY : WAITING_FOR_ANOTHER_PLAYER;
            fillGameText();
            winner = null;
            resetPlayers();
            doneCount = 0;
            startTime = 0L;
        } finally {
            gameRwLock.writeLock().unlock();
            doneLock.writeLock().unlock();
        }
    }

    public void incrementDoneCount() {
        doneLock.writeLock().lock();
        try {
            doneCount++;
        } finally {
            doneLock.writeLock().unlock();
        }
    }
}
