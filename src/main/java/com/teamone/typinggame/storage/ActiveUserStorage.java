package com.teamone.typinggame.storage;

import com.teamone.typinggame.models.Player;
import com.teamone.typinggame.models.User;

import java.util.HashMap;
import java.util.Map;

public class ActiveUserStorage {
    private static Map<Long, User> activeUsers;
    private static ActiveUserStorage instance;

    private ActiveUserStorage() {
        activeUsers = new HashMap<>();
    }

    public static synchronized ActiveUserStorage getInstance() {
        if (instance == null) {
            instance = new ActiveUserStorage();
        }
        return instance;
    }

    public synchronized void addUser(User user) {
        activeUsers.put(user.getUserID(), user);
    }

    public synchronized boolean contains(User user) {
        return activeUsers.containsKey(user.getUserID());
    }

    public synchronized void removeUser(User user) {
        activeUsers.remove(user.getUserID());
    }

    public synchronized void removeUser(Long userID) {
        activeUsers.remove(userID);
    }
}
