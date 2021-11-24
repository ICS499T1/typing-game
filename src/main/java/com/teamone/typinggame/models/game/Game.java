package com.teamone.typinggame.models.game;

import com.teamone.typinggame.configuration.GameConfig;
import com.teamone.typinggame.models.GameStatus;
import com.teamone.typinggame.models.Player;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import static com.teamone.typinggame.models.GameStatus.*;

@RequiredArgsConstructor
@Component
public class Game extends GameInterface {
    private final ReadWriteLock gameRwLock = new ReentrantReadWriteLock();
    private final ReadWriteLock playerSetLock = new ReentrantReadWriteLock();
    private final ReadWriteLock doneLock = new ReentrantReadWriteLock();

    @Getter
    private Map<String, Player> players;

    private Player winner;

    @Getter
    private Integer playerCount;

    private GameConfig gameConfig = new GameConfig();

    public Game(String gameId) {
        initialText();
        setGameId(gameId);
        this.players = new HashMap<>();
        this.playerCount = 0;
        setStatus(WAITING_FOR_ANOTHER_PLAYER);
        setDoneCount(0);
    }

    public void reassignPlayers() {
        if (getPlayerCount() >= 1) {
            AtomicInteger playerNum = new AtomicInteger(0);
            players.forEach((sessionIdObj, playerObj) -> playerObj.setPlayerNumber(playerNum.incrementAndGet()));
        }
    }

    public boolean containsHost() {
        playerSetLock.writeLock().lock();
        try {
            for (Player player : players.values()) {
                if (player.getPlayerNumber() == 1) return true;
            }
            return false;
        } finally {
            playerSetLock.writeLock().unlock();
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
            if (player == null) {
                winner = null;
                return;
            }
            winner = player;
            player.setWinner(true);
            incrementDoneCount();
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

    public boolean removePlayer(String sessionId) {
        playerSetLock.writeLock().lock();
        try {
            Player removedPlayer = players.remove(sessionId);
            if (removedPlayer != null) {
                playerCount--;
                return true;
            }
            return false;
        } finally {
            playerSetLock.writeLock().unlock();
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

    public void resetPlayers() {
        playerSetLock.writeLock().lock();
        try {
            players.forEach((sessionId, player) -> player.reset());
        } finally {
            playerSetLock.writeLock().unlock();
        }
    }

    public void reset() {
        doneLock.writeLock().lock();
        gameRwLock.writeLock().lock();
        try {
            setStatus(COUNTDOWN);
            fillGameText();
            winner = null;
            resetPlayers();
            setDoneCount(0);
            setStartTime(0L);
        } finally {
            gameRwLock.writeLock().unlock();
            doneLock.writeLock().unlock();
        }
    }

    @Override
    public String toString() {
        return "Game{" +
                "players=" + players +
                ", winner=" + winner +
                ", playerCount=" + playerCount +
                '}';
    }
}
