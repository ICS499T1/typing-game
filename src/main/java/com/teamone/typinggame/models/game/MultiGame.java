package com.teamone.typinggame.models.game;

import com.teamone.typinggame.configuration.GameConfig;
import com.teamone.typinggame.models.Player;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import static com.teamone.typinggame.models.GameStatus.COUNTDOWN;
import static com.teamone.typinggame.models.GameStatus.WAITING_FOR_ANOTHER_PLAYER;

@RequiredArgsConstructor
@Component
public class MultiGame extends GameInterface {

    @Getter
    private Map<String, Player> players;

    private Player winner;

    @Getter
    private Integer playerCount;

    private GameConfig gameConfig = new GameConfig();

    /**
     * Basic constructor for game initialization.
     *
     * @param gameId - game id
     */
    public MultiGame(String gameId) {
        setGameId(gameId);
        this.players = new HashMap<>();
        this.playerCount = 0;
        setStatus(WAITING_FOR_ANOTHER_PLAYER);
        setDoneCount(0);
    }

    /**
     * Reassigns the players if the player leaves.
     */
    public void reassignPlayers() {
        if (getPlayerCount() >= 1) {
            AtomicInteger playerNum = new AtomicInteger(0);
            players.forEach((sessionIdObj, playerObj) -> playerObj.setPlayerNumber(playerNum.incrementAndGet()));
        }
    }

    /**
     * Checks if the host is in the game.
     *
     * @return boolean - true if host is there, false otherwise.
     */
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

    /**
     * Adds a player to the list of players.
     *
     * @param sessionId - player's session id
     * @param player    - player
     * @return boolean - true if added successfully, false otherwise
     */
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

    /**
     * Removes a player from the game.
     *
     * @param sessionId - player's session id
     * @return boolean - true if the player is removed, false otherwise.
     */
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

    /**
     * Gets a player based on their session id.
     *
     * @param sessionId - player's session id
     * @return Player
     */
    public Player getPlayer(String sessionId) {
        playerSetLock.readLock().lock();
        try {
            return players.get(sessionId);
        } finally {
            playerSetLock.readLock().unlock();
        }
    }

    /**
     * Checks if the list contains a player based on their session id.
     *
     * @param sessionId - player's session id
     * @return boolean - true if the player is there, false otherwise
     */
    public boolean containsPlayer(String sessionId) {
        playerSetLock.readLock().lock();
        try {
            return players.containsKey(sessionId);
        } finally {
            playerSetLock.readLock().unlock();
        }
    }

    /**
     * Resets each player.
     */
    public void resetPlayers() {
        playerSetLock.writeLock().lock();
        try {
            players.forEach((sessionId, player) -> player.reset());
        } finally {
            playerSetLock.writeLock().unlock();
        }
    }

    /**
     * Resets each game.
     */
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
