package com.teamone.typinggame.controllers;

import com.teamone.typinggame.exceptions.*;
import com.teamone.typinggame.models.Game;
import com.teamone.typinggame.models.GameStatus;
import com.teamone.typinggame.models.User;
import com.teamone.typinggame.services.game.GameServiceImpl;
import com.teamone.typinggame.services.game.PlayerServiceImpl;
import lombok.Data;
import org.springframework.messaging.handler.annotation.*;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Data
public class GameController {
    private final GameServiceImpl gameService;
    private final PlayerServiceImpl playerService;
    private final SimpMessagingTemplate simpMessagingTemplate;

//    @PreAuthorize("#user.username == authentication.name")
    @GetMapping("/get-game-id")
    public String getGameId() {
        System.out.println("get game id security context: " + SecurityContextHolder.getContext().getAuthentication());
        return gameService.generateGameId();
    }

//    @MessageExceptionHandler()
    @MessageMapping("/create/{gameId}")
    public void createGame(@DestinationVariable(value = "gameId") String gameId, @Header("simpSessionId") String sessionId, @Header("simpUser") UsernamePasswordAuthenticationToken principal, User user) throws UserNotFoundException, ActiveUserException, GameAlreadyExistsException {
        if (principal.getName().equals(user.getUsername())) {
            Game game = gameService.createGame(gameId, sessionId, user);
//            simpMessagingTemplate.convertAndSend("/game/join/" + gameId, game);
//            simpMessagingTemplate.convertAndSend("/game/gameText/" + gameId, game.getGameText());
            simpMessagingTemplate.convertAndSend("/game/status/" + gameId, game);
        }
    }

    // TODO add check for making sure timer can only be started by player who created game (probably by checking if sessionId matches player1's sessionId)
    @MessageMapping("/timer/{gameId}")
    public void startTimer(@DestinationVariable(value = "gameId") String gameId, @Header("simpSessionId") String sessionId) throws InvalidGameStateException, GameNotFoundException {
        Game game = gameService.startTimer(gameId, sessionId);
        simpMessagingTemplate.convertAndSend("/game/status/" + gameId, game);
    }

    // TODO add check for making sure game can only be started by player who created game (probably by checking if sessionId matches player1's sessionId)
    @MessageMapping("/start/{gameId}")
    public void startGame(@DestinationVariable(value = "gameId") String gameId, @Header("simpUser") UsernamePasswordAuthenticationToken principal) throws InvalidGameStateException, GameNotFoundException {
        System.out.println("Entering start game controller");
        Game game = gameService.gameStart(gameId);
        simpMessagingTemplate.convertAndSend("/game/gameplay/" + gameId, game);
        simpMessagingTemplate.convertAndSend("/game/status/" + gameId, game);
    }

    @MessageMapping("/join/{gameId}")
    public void joinGame(@Header("simpSessionId") String sessionId, @DestinationVariable(value = "gameId") String gameId, @Header("simpUser") UsernamePasswordAuthenticationToken principal, User user) throws UserNotFoundException, InvalidGameStateException, GameNotFoundException, ActiveUserException {
        if (principal.getName().equals(user.getUsername())) {
            Game game = gameService.connectToGame(sessionId, gameId, user);
//            simpMessagingTemplate.convertAndSend("/game/join/" + gameId, game);
//            simpMessagingTemplate.convertAndSend("/game/gameText/" + gameId, game.getGameText());
            simpMessagingTemplate.convertAndSend("/game/status/" + gameId, game);
        }
    }

    @MessageMapping("/gameplay/{gameId}")
    public void gameplay(@Header("simpSessionId") String sessionId, @DestinationVariable(value = "gameId") String gameId, @Header("simpUser") UsernamePasswordAuthenticationToken principal, Character input) throws InvalidGameStateException, PlayerNotFoundException, GameNotFoundException, UserNotFoundException {
        Game game = gameService.gamePlay(sessionId, gameId, input);
        simpMessagingTemplate.convertAndSend("/game/gameplay/" + game.getGameId(), game);
        if (game.getStatus() == GameStatus.COMPLETED) {
            simpMessagingTemplate.convertAndSend("/game/status/" + gameId, game);
        }
        if (game.getPlayer(sessionId).getEndTime() != 0) {
            simpMessagingTemplate.convertAndSend("/game/playerStatus/" + gameId + "/" + sessionId, 1);
        }
    }

    @MessageMapping("/end/{gameId}")
    public void endGame(@Header("simpSessionId") String sessionId, @DestinationVariable(value = "gameId") String gameId) throws InvalidGameStateException, GameNotFoundException {
        Game game = gameService.gameEnd(gameId);
//        simpMessagingTemplate.convertAndSend("/game/join/" + game.getGameId(), game);
//        simpMessagingTemplate.convertAndSend("/game/gameText/" + gameId, game.getGameText());
        simpMessagingTemplate.convertAndSend("/game/status/" + gameId, game);
    }
}
