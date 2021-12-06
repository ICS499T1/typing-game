package com.teamone.typinggame.storage;

import com.teamone.typinggame.models.User;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class ActiveUserStorage {
    private static Map<String, User> activeUsers;
    private static ActiveUserStorage instance;
    private final static ReadWriteLock activeUserLock = new ReentrantReadWriteLock();

    /**
     * Singleton initialization of the active user storage.
     */
    private ActiveUserStorage() {
        activeUserLock.writeLock().lock();
        try {
            activeUsers = new HashMap<>();
        } finally {
            activeUserLock.writeLock().unlock();
        }
    }

    /**
     * Returns an instance of active user storage.
     *
     * @return ActiveUserStorage
     */
    public static ActiveUserStorage getInstance() {
        if (instance == null) {
            instance = new ActiveUserStorage();
        }
        return instance;
    }

    /**
     * Returns a user based on the username.
     *
     * @param username - input username
     * @return User
     */
    public User getUser(String username) {
        activeUserLock.writeLock().lock();
        try {
            return activeUsers.get(username);
        } finally {
            activeUserLock.writeLock().unlock();
        }
    }

    /**
     * Adds user to the storage.
     *
     * @param user - user to be added
     */
    public void addUser(User user) {
        activeUserLock.writeLock().lock();
        try {
            activeUsers.put(user.getUsername(), user);
        } finally {
            activeUserLock.writeLock().unlock();
        }
    }

    /**
     * Checks if the storage contains a user.
     *
     * @param user - input user
     * @return boolean - true if the user exists, false otherwise
     */
    public boolean contains(User user) {
        activeUserLock.readLock().lock();
        try {
            return activeUsers.containsKey(user.getUsername());
        } finally {
            activeUserLock.readLock().unlock();
        }
    }

    /**
     * Removes a user from the active storage.
     *
     * @param username - username of the user to be removed
     */
    public void removeUser(String username) {
        activeUserLock.writeLock().lock();
        try {
            activeUsers.remove(username);
        } finally {
            activeUserLock.writeLock().unlock();
        }
    }
}
