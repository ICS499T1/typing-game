package com.teamone.typinggame.controllers;

import com.teamone.typinggame.exceptions.*;
import com.teamone.typinggame.models.Game;
import com.teamone.typinggame.models.User;
import com.teamone.typinggame.services.game.GameServiceImpl;
import com.teamone.typinggame.services.game.PlayerServiceImpl;
import lombok.Data;
import org.springframework.messaging.handler.annotation.*;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Data
public class GameController {
    private final GameServiceImpl gameService;
    private final PlayerServiceImpl playerService;
    private final SimpMessagingTemplate simpMessagingTemplate;

//    @MessageMapping("/create")
//    public void createGame(@Header("simpSessionId") String sessionId, User user) throws UserNotFoundException, ActiveUserException {
//        Game game = gameService.createGame(sessionId, user);
//        simpMessagingTemplate.convertAndSend("/game/" + user.getUsername(), game);
//    }

//    @PreAuthorize("#user.username == authentication.name")
    @GetMapping("/get-game-id")
    public String getGameId() {
        System.out.println("get game id security context: " + SecurityContextHolder.getContext().getAuthentication());
        return gameService.generateGameId();
    }

    @MessageMapping("/create/{gameId}")
    public void createGame(@DestinationVariable(value = "gameId") String gameId, @Header("simpSessionId") String sessionId, @Header("simpUser") UsernamePasswordAuthenticationToken principal, User user) throws UserNotFoundException, ActiveUserException, GameAlreadyExistsException {
        if (principal.getName().equals(user.getUsername())) {
            Game game = gameService.createGame(gameId, sessionId, user);
            simpMessagingTemplate.convertAndSend("/game/join/" + gameId, game);
        }
    }

    // TODO add check for making sure game can only be started by player who created game (probably by checking if sessionId matches player1's sessionId)
    @MessageMapping("/start/{gameId}")
    public void startGame(@DestinationVariable(value = "gameId") String gameId, @Header("simpUser") UsernamePasswordAuthenticationToken principal) throws InvalidGameStateException, GameNotFoundException {
        System.out.println(principal);
        Game game = gameService.gameStart(gameId);
        simpMessagingTemplate.convertAndSend("/game/join/" + game.getGameId(), game);
    }

    @MessageMapping("/join/{gameId}")
//    @SendTo("/game/join")
    public void joinGame(@Header("simpSessionId") String sessionId, @DestinationVariable(value = "gameId") String gameId, @Header("simpUser") UsernamePasswordAuthenticationToken principal, User user) throws UserNotFoundException, InvalidGameStateException, GameNotFoundException, ActiveUserException {
//        System.out.println("GameController[joinGame]: " + user);
        if (principal.getName().equals(user.getUsername())) {
            Game game = gameService.connectToGame(sessionId, gameId, user);
            simpMessagingTemplate.convertAndSend("/game/join/" + game.getGameId(), game);
        }
    }

    @MessageMapping("/gameplay/{gameId}")
    public void gameplay(@Header("simpSessionId") String sessionId, @DestinationVariable(value = "gameId") String gameId, @Header("simpUser") UsernamePasswordAuthenticationToken principal, Character input) throws InvalidGameStateException, PlayerNotFoundException, GameNotFoundException {
//        System.out.println(input);
//        if (input == '\b') {
//            System.out.println("It's a backspace");
//        }
        Game game = gameService.gamePlay(sessionId, gameId, input);
        simpMessagingTemplate.convertAndSend("/game/join/" + game.getGameId(), game);
    }
//    @SendTo("/game/{gameid}")
//    public Game gameMove()

}
