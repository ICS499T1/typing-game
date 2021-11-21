package com.teamone.typinggame.storage;

import com.teamone.typinggame.models.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class PlayerStorage {
    private static Map<String, Player> players;
    private static PlayerStorage instance;
    private final static ReadWriteLock playerStorageLock = new ReentrantReadWriteLock();

    private PlayerStorage() {
        playerStorageLock.writeLock().lock();
        try {
            players = new HashMap<>();
        } finally {
            playerStorageLock.writeLock().unlock();
        }
    }

    public static PlayerStorage getInstance() {
        if (instance == null) {
            instance = new PlayerStorage();
        }
        return instance;
    }

    public void addPlayer(String sessionId, Player player) {
        playerStorageLock.writeLock().lock();
        try {
            players.put(sessionId, player);
        } finally {
            playerStorageLock.writeLock().unlock();
        }
    }


    public boolean contains(Player player) {
        playerStorageLock.readLock().lock();
        try {
            return players.containsValue(player);
        } finally {
            playerStorageLock.readLock().unlock();
        }
    }

    public boolean contains(String sessionId) {
        playerStorageLock.readLock().lock();
        try {
            return players.containsKey(sessionId);
        } finally {
            playerStorageLock.readLock().unlock();
        }
    }

    public Player removePlayer(String sessionId) {
        playerStorageLock.writeLock().lock();
        try {
            return players.remove(sessionId);
        } finally {
            playerStorageLock.writeLock().unlock();
        }
    }
}
