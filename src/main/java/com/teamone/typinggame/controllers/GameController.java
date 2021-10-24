package com.teamone.typinggame.controllers;

import com.teamone.typinggame.models.Game;
import com.teamone.typinggame.models.User;
import com.teamone.typinggame.services.GameServiceImpl;
import lombok.Data;
import org.springframework.messaging.handler.annotation.*;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Data
public class GameController {
    private final GameServiceImpl gameService;

    @MessageMapping("/create")
    @SendTo("/game/sendhere")
    public User createGame(@Header("simpSessionId") String sessionId, User user) {
        System.out.println(user);

        return new User("testing server side user");
    }

//    @SendTo("/game/{gameid}")
//    public Game gameMove()

}
