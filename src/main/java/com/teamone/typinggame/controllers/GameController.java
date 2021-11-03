package com.teamone.typinggame.controllers;

import com.teamone.typinggame.exceptions.*;
import com.teamone.typinggame.models.Game;
import com.teamone.typinggame.models.User;
import com.teamone.typinggame.services.game.GameServiceImpl;
import com.teamone.typinggame.services.game.PlayerServiceImpl;
import lombok.Data;
import org.springframework.messaging.handler.annotation.*;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Data
public class GameController {
    private final GameServiceImpl gameService;
    private final PlayerServiceImpl playerService;
    private final SimpMessagingTemplate simpMessagingTemplate;

    @GetMapping("/usertest")
    public void userTest(@Header("simpSessionId") String sessionId, @RequestBody User user) throws UserNotFoundException, ActiveUserException {
        if (user != null) {
            Game game = gameService.createGame(sessionId, user);
            System.out.println("Game ID: " + game.getGameId());
        } else System.out.println("The user is null.");
    }

    @MessageMapping("/create")
    public void createGame(@Header("simpSessionId") String sessionId, User user) throws UserNotFoundException, ActiveUserException {
        Game game = gameService.createGame(sessionId, user);
        simpMessagingTemplate.convertAndSend("/game/" + user.getUsername(), game);
    }

    @MessageMapping("/join/{gameId}")
//    @SendTo("/game/join")
    public void joinGame(@Header("simpSessionId") String sessionId, @DestinationVariable(value = "gameId") String gameId, User user) throws UserNotFoundException, InvalidGameStateException, GameNotFoundException, ActiveUserException {
        System.out.println("GameController[joinGame]: " + user);
        Game game = gameService.connectToGame(sessionId, gameId, user);
        simpMessagingTemplate.convertAndSend("/game/join/" + game.getGameId(), game);
    }

//    @SendTo("/game/{gameid}")
//    public Game gameMove()

}
