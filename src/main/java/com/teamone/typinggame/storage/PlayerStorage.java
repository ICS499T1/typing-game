package com.teamone.typinggame.storage;

import com.teamone.typinggame.models.Player;

import java.util.HashMap;
import java.util.Map;

public class PlayerStorage {
    private static Map<String, Player> players;
    private static PlayerStorage instance;

    private PlayerStorage() {
        players = new HashMap<>();
    }

    public static synchronized PlayerStorage getInstance() {
        if (instance == null) {
            instance = new PlayerStorage();
        }
        return instance;
    }

    public synchronized void addPlayer(String sessionId, Player player) {
        players.put(sessionId, player);
    }

//    public Player getPlayer(Long userID) {
//        return players.get(userID);
//    }

    public synchronized boolean contains(Player player) {
        return players.containsValue(player);
    }

    public synchronized boolean contains(String sessionId) {
        return players.containsKey(sessionId);
    }

    public synchronized Player removePlayer(String sessionId) {
        return players.remove(sessionId);
    }
}
