package com.teamone.typinggame.listeners;

import com.teamone.typinggame.controllers.MultiGameController;
import com.teamone.typinggame.models.Player;
import com.teamone.typinggame.models.game.MultiGame;
import com.teamone.typinggame.models.game.GameInterface;
import com.teamone.typinggame.services.game.SingleGameServiceImpl;
import com.teamone.typinggame.storage.GameStorage;
import com.teamone.typinggame.storage.PlayerStorage;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

@Component
@Slf4j
public class StompDisconnectedEventListener implements ApplicationListener<SessionDisconnectEvent> {

    @Autowired
    private MultiGameController multiGameController;

    @Autowired
    private SingleGameServiceImpl singleGameService;

    private final PlayerStorage playerStorage = PlayerStorage.getInstance();
    private final GameStorage gameStorage = GameStorage.getInstance();

    @SneakyThrows
    @Override
    public void onApplicationEvent(SessionDisconnectEvent event) {
        if (event.getMessage().getHeaders().containsKey("simpUser")) {
            String sessionId = (String) event.getMessage().getHeaders().get("simpSessionId");
            Player player = playerStorage.getPlayer(sessionId);
            if (player != null) {
                System.out.println("GETTING GAME");
                String gameId = player.getGameId();
                GameInterface game = gameStorage.getGame(gameId);
                if (game != null) {
                    System.out.println("GAME NOT NULL");
                    if (gameStorage.getGame(gameId) instanceof MultiGame) {
                        System.out.println("CALLING REMOVE");
                        multiGameController.removePlayer(sessionId);
                    } else {
                        singleGameService.removePlayer(sessionId);
                    }
                } else log.error("onApplicationEvent -> game is null");
            } else log.error("onApplicationEvent -> player is null");
        }
    }
}