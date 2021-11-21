package com.teamone.typinggame.storage;

import com.teamone.typinggame.models.Player;
import com.teamone.typinggame.models.User;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class ActiveUserStorage {
    private static Map<String, User> activeUsers;
    private static ActiveUserStorage instance;
    private final static ReadWriteLock activeUserLock = new ReentrantReadWriteLock();

    private ActiveUserStorage() {
        activeUserLock.writeLock().lock();
        try {
            activeUsers = new HashMap<>();
        } finally {
            activeUserLock.writeLock().unlock();
        }
    }

    public static ActiveUserStorage getInstance() {
        if (instance == null) {
            instance = new ActiveUserStorage();
        }
        return instance;
    }

    public void addUser(User user) {
        activeUserLock.writeLock().lock();
        try {
            activeUsers.put(user.getUsername(), user);
        } finally {
            activeUserLock.writeLock().unlock();
        }
    }

    public boolean contains(User user) {
        activeUserLock.readLock().lock();
        try {
            return activeUsers.containsKey(user.getUsername());
        } finally {
            activeUserLock.readLock().unlock();
        }
    }

    public void removeUser(User user) {
        activeUserLock.writeLock().lock();
        try {
            activeUsers.remove(user.getUsername());
        } finally {
            activeUserLock.writeLock().unlock();
        }
    }

    public void removeUser(String username) {
        activeUserLock.writeLock().lock();
        try {
            activeUsers.remove(username);
        } finally {
            activeUserLock.writeLock().unlock();
        }
    }
}
