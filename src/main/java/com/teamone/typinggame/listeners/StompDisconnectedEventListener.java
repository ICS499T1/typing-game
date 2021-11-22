package com.teamone.typinggame.listeners;

import com.teamone.typinggame.services.game.GameServiceImpl;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.util.Map;

@Component
public class StompDisconnectedEventListener implements ApplicationListener<SessionDisconnectEvent> {

    @Autowired
    private GameServiceImpl gameService;

    @SneakyThrows
    @Override
    public void onApplicationEvent(SessionDisconnectEvent event) {
        System.out.println(event.getMessage().getHeaders());
        if (event.getMessage().getHeaders().containsKey("simpUser")) {
            String sessionId = (String) event.getMessage().getHeaders().get("simpSessionId");
            System.out.println("Simp session id: " + sessionId);
            gameService.removePlayer(sessionId);
        }
    }
}