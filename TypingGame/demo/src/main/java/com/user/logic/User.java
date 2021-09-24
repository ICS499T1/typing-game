package com.user.logic;

import com.gameplay.logic.GameSpawner;

public class User {
    private String username;
    private Stats userStats;
    private int userID; //create another class that generates and increments userIDs (get userID from database)
    private boolean inAnActiveSession;

    public synchronized boolean isInAnActiveSession() {
        return inAnActiveSession;
    }

    public synchronized void setInAnActiveSession(boolean inAnActiveSession) {
        this.inAnActiveSession = inAnActiveSession;
    }

    public User(String username) {
        this.username = username;
        getUserID();
        getStats();
    }

    private void getUserID() {
        // create a new entry in database if user doesn't exist and use incremented userID and assign it here
        // otherwise get userID of username and assign it
    }

    private void getStats() {
        // find stats with same userID and create a new com.user.logic.Stats instance and assign it to userStats
    }
}
