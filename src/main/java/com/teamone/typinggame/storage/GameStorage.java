package com.teamone.typinggame.storage;

import com.teamone.typinggame.models.Game;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class GameStorage {
    private static Map<String, Game> games;
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

    public Game getGame(String gameId) {
        gameStorageLock.readLock().lock();
        try {
            return games.get(gameId);
        } finally {
            gameStorageLock.readLock().unlock();
        }
    }

    public void addGame(Game game) {
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

    public void removeGame(Game game) {
        gameStorageLock.writeLock().lock();
        try {
            games.remove(game.getGameId());
        } finally {
            gameStorageLock.writeLock().unlock();
        }
    }
}
