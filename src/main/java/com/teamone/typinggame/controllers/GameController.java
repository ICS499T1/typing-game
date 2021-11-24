package com.teamone.typinggame.controllers;

import com.teamone.typinggame.exceptions.*;
import com.teamone.typinggame.models.game.Game;
import com.teamone.typinggame.models.GameStatus;
import com.teamone.typinggame.models.User;
import com.teamone.typinggame.repositories.KeyStatsRepository;
import com.teamone.typinggame.services.game.GameServiceImpl;
import com.teamone.typinggame.services.game.PlayerServiceImpl;
import lombok.Data;
import org.springframework.messaging.handler.annotation.*;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Data
public class GameController {
    private final GameServiceImpl gameService;
    private final PlayerServiceImpl playerService;
    private final SimpMessagingTemplate simpMessagingTemplate;
    private final KeyStatsRepository keyStatsRepository;

//    @PreAuthorize("#user.username == authentication.name")
    @GetMapping("/get-game-id")
    public String getGameId() {
        return gameService.generateGameId();
    }

//    @MessageExceptionHandler()
    @MessageMapping("/create/{gameId}/{session}")
    public void createGame(@DestinationVariable(value = "gameId") String gameId, @Header("simpSessionId") String sessionId, @Header("simpUser") UsernamePasswordAuthenticationToken principal, User user) throws UserNotFoundException, ActiveUserException, GameAlreadyExistsException {
        if (principal.getName().equals(user.getUsername())) {
            Game game = gameService.createGame(gameId, sessionId, user);
//            simpMessagingTemplate.convertAndSend("/game/join/" + gameId, game);
//            simpMessagingTemplate.convertAndSend("/game/gameText/" + gameId, game.getGameText());
            simpMessagingTemplate.convertAndSend("/game/status/" + gameId, game);
        }
    }

    // TODO add check for making sure timer can only be started by player who created game (probably by checking if sessionId matches player1's sessionId)
//    @MessageMapping("/timer/{gameId}/{session}")
//    public void startTimer(@DestinationVariable(value = "gameId") String gameId, @Header("simpSessionId") String sessionId) throws InvalidGameStateException, GameNotFoundException, UnsupportedGameTypeException {
//        Game game = gameService.startTimer(gameId, sessionId);
//        simpMessagingTemplate.convertAndSend("/game/status/" + gameId, game);
//    }

    // TODO add check for making sure game can only be started by player who created game (probably by checking if sessionId matches player1's sessionId)
    @MessageMapping("/start/{gameId}/{session}")
    public void startGame(@DestinationVariable(value = "gameId") String gameId, @Header("simpUser") UsernamePasswordAuthenticationToken principal) throws InvalidGameStateException, GameNotFoundException, UnsupportedGameTypeException {
        System.out.println("Entering start game controller");
        Game game = gameService.gameStart(gameId);
        simpMessagingTemplate.convertAndSend("/game/gameplay/" + gameId, game);
        simpMessagingTemplate.convertAndSend("/game/status/" + gameId, game);
    }

    @MessageMapping("/join/{gameId}/{session}")
    public void joinGame(@Header("simpSessionId") String sessionId, @DestinationVariable(value = "gameId") String gameId, @Header("simpUser") UsernamePasswordAuthenticationToken principal, User user) throws UserNotFoundException, InvalidGameStateException, GameNotFoundException, ActiveUserException, UnsupportedGameTypeException {
        if (principal.getName().equals(user.getUsername())) {
            Game game = gameService.connectToGame(sessionId, gameId, user);
//            simpMessagingTemplate.convertAndSend("/game/join/" + gameId, game);
//            simpMessagingTemplate.convertAndSend("/game/gameText/" + gameId, game.getGameText());
            simpMessagingTemplate.convertAndSend("/game/status/" + gameId, game);
        }
    }

    @MessageMapping("/gameplay/{gameId}/{session}")
    public void gameplay(@Header("simpSessionId") String sessionId, @DestinationVariable(value = "gameId") String gameId, @Header("simpUser") UsernamePasswordAuthenticationToken principal, Character input) throws InvalidGameStateException, PlayerNotFoundException, GameNotFoundException, UnsupportedGameTypeException {
        Game game = gameService.gamePlay(sessionId, gameId, input);
        simpMessagingTemplate.convertAndSend("/game/gameplay/" + game.getGameId(), game);
        if (game.getStatus() == GameStatus.READY || game.getStatus() == GameStatus.WAITING_FOR_ANOTHER_PLAYER) {
            simpMessagingTemplate.convertAndSend("/game/status/" + gameId, game);
        }
        if (game.getPlayer(sessionId).getEndTime() != 0) {
            simpMessagingTemplate.convertAndSend("/game/playerStatus/" + gameId + "/" + sessionId, 1);
        }
    }

    @MessageMapping("/timer/{gameId}/{session}")
    public void startTimer(@DestinationVariable(value = "gameId") String gameId) throws InvalidGameStateException, GameNotFoundException, UnsupportedGameTypeException {
        Game game = gameService.startTimer(gameId);
//        simpMessagingTemplate.convertAndSend("/game/join/" + game.getGameId(), game);
//        simpMessagingTemplate.convertAndSend("/game/gameText/" + gameId, game.getGameText());
        simpMessagingTemplate.convertAndSend("/game/status/" + gameId, game);
    }

    public void removePlayer(String sessionId) throws PlayerNotFoundException, UnsupportedGameTypeException, GameNotFoundException {
        Game game = gameService.removePlayer(sessionId);
        simpMessagingTemplate.convertAndSend("/game/status/" + game.getGameId(), game);
    }

    @MessageExceptionHandler
    @SendTo("/game/errors/{gameId}/{session}")
    public String handleException(Throwable exception, @DestinationVariable(value = "session") String session, @DestinationVariable(value = "gameId") String gameId) {
        return exception.getMessage();
    }
}
