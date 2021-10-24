package com.teamone.typinggame.storage;

import com.teamone.typinggame.models.Game;

import java.util.HashMap;
import java.util.Map;

public class GameStorage {
    private static Map<String, Game> games;
    private static GameStorage instance;

    private GameStorage() {
        games = new HashMap<>();
    }

    public static synchronized GameStorage getInstance() {
        if (instance == null) {
            instance = new GameStorage();
        }
        return instance;
    }

    public synchronized Game getGame(String gameId) {
        return games.get(gameId);
    }

    public synchronized void addGame(Game game) {
        games.put(game.getGameId(), game);
    }

    public synchronized boolean contains(String gameId) {
        return games.containsKey(gameId);
    }

    public synchronized void removeGame(Game game) {
        games.remove(game.getGameId());
    }
}
