package com.teamone.typinggame.controllers;

import com.teamone.typinggame.exceptions.ActiveUserException;
import com.teamone.typinggame.exceptions.GameNotFoundException;
import com.teamone.typinggame.exceptions.InvalidGameStateException;
import com.teamone.typinggame.exceptions.UserNotFoundException;
import com.teamone.typinggame.models.Game;
import com.teamone.typinggame.models.User;
import com.teamone.typinggame.services.GameServiceImpl;
import lombok.Data;
import org.springframework.messaging.handler.annotation.*;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.HtmlUtils;

@RestController
@Data
public class GameController {
    private final GameServiceImpl gameService;
    private final SimpMessagingTemplate simpMessagingTemplate;

    @MessageMapping("/create")
    @SendTo("/game/initialize")
    public Game createGame(@Header("simpSessionId") String sessionId, User user) throws UserNotFoundException, ActiveUserException {
        Game game = gameService.createGame(sessionId, user);
        System.out.println("Game Id: " + game.getGameId());
        return game;
    }

    @MessageMapping("/join/{gameId}")
//    @SendTo("/game/join")
    public void joinGame(@Header("simpSessionId") String sessionId, @DestinationVariable(value = "gameId") String gameId, User user) throws UserNotFoundException, InvalidGameStateException, GameNotFoundException, ActiveUserException {
        System.out.println("GameController: " + user);
        Game game = gameService.connectToGame(sessionId, gameId, user);
        simpMessagingTemplate.convertAndSend("/game/join/" + game.getGameId(), game);
    }

    @MessageMapping("/start")
    @SendTo("/game/start")
    public Game startGame(String gameId) throws InvalidGameStateException, GameNotFoundException {
        Game game = gameService.gameStart(gameId);
        return game;
    }

//    @SendTo("/game/{gameid}")
//    public Game gameMove()

}
