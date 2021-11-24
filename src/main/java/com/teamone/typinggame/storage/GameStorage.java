package com.teamone.typinggame.storage;

import com.teamone.typinggame.models.game.Game;
import com.teamone.typinggame.models.game.GameInterface;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class GameStorage {
    private static Map<String, GameInterface> games;
    private static GameStorage instance;
    private final static ReadWriteLock gameStorageLock = new ReentrantReadWriteLock();

    private GameStorage() {
        gameStorageLock.writeLock().lock();
        try {
            games = new HashMap<>();
        } finally {
            gameStorageLock.writeLock().unlock();
        }
    }

    public static GameStorage getInstance() {
        if (instance == null) {
            instance = new GameStorage();
        }
        return instance;
    }

    public GameInterface getGame(String gameId) {
        gameStorageLock.readLock().lock();
        try {
            return games.get(gameId);
        } finally {
            gameStorageLock.readLock().unlock();
        }
    }

    public void addGame(GameInterface game) {
        gameStorageLock.writeLock().lock();
        try {
            games.put(game.getGameId(), game);
        } finally {
            gameStorageLock.writeLock().unlock();
        }
    }

    public boolean contains(String gameId) {
        gameStorageLock.readLock().lock();
        try {
            return games.containsKey(gameId);
        } finally {
            gameStorageLock.readLock().unlock();
        }
    }

    public void removeGame(GameInterface game) {
        gameStorageLock.writeLock().lock();
        try {
            games.remove(game.getGameId());
        } finally {
            gameStorageLock.writeLock().unlock();
        }
    }

    public void printGames() {
        gameStorageLock.readLock().lock();
        try {
            games.forEach((gameId, game) -> {
                System.out.println("GAME: " + gameId);
            });
        } finally {
            gameStorageLock.readLock().unlock();
        }
    }
}
