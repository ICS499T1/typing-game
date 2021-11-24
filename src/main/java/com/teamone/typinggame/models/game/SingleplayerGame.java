package com.teamone.typinggame.models.game;

import com.teamone.typinggame.configuration.GameConfig;
import com.teamone.typinggame.models.Player;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import static com.teamone.typinggame.models.GameStatus.READY;

@RequiredArgsConstructor
@Component
public class SingleplayerGame extends GameInterface {

    private Player player;

    private GameConfig gameConfig = new GameConfig();

    public SingleplayerGame(String gameId, Player player) {
        fillGameText();
        this.player = player;
        setGameId(gameId);
        setStatus(READY);
        setDoneCount(0);
    }

    public Player getPlayer() {
        playerSetLock.readLock().lock();
        try {
            return player;
        } finally {
            playerSetLock.readLock().unlock();
        }
    }

    private void resetPlayer() {
        playerSetLock.writeLock().lock();
        try {
            player.reset();
        } finally {
            playerSetLock.writeLock().unlock();
        }
    }

    public void reset() {
        doneLock.writeLock().lock();
        gameRwLock.writeLock().lock();
        try {
            setStatus(READY);
            fillGameText();
            resetPlayer();
            setDoneCount(0);
            setStartTime(0L);
        } finally {
            gameRwLock.writeLock().unlock();
            doneLock.writeLock().unlock();
        }
    }
}

