package com.teamone.typinggame.services.game;

import com.teamone.typinggame.models.Player;
import com.teamone.typinggame.storage.PlayerStorage;
import org.springframework.stereotype.Service;

@Service
public class PlayerServiceImpl {
    private final PlayerStorage playerStorage = PlayerStorage.getInstance();

    public synchronized Player removePlayer(String sessionId) {
        return playerStorage.removePlayer(sessionId);
    }
}
