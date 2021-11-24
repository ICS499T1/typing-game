package com.teamone.typinggame.controllers;

import com.teamone.typinggame.exceptions.*;
import com.teamone.typinggame.models.GameStatus;
import com.teamone.typinggame.models.User;
import com.teamone.typinggame.models.game.SingleplayerGame;
import com.teamone.typinggame.repositories.KeyStatsRepository;
import com.teamone.typinggame.services.game.PlayerServiceImpl;
import com.teamone.typinggame.services.game.SingleGameServiceImpl;
import lombok.Data;
import org.springframework.messaging.handler.annotation.*;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.RestController;

@Data
@RestController
public class SinglePlayerGameController {

    private final SingleGameServiceImpl singleGameService;
    private final PlayerServiceImpl playerService;
    private final SimpMessagingTemplate simpMessagingTemplate;
    private final KeyStatsRepository keyStatsRepository;

    @MessageMapping("/create/single/{gameId}/{session}")
    public void createGame(@DestinationVariable(value = "gameId") String gameId, @Header("simpSessionId") String sessionId, @Header("simpUser") UsernamePasswordAuthenticationToken principal, User user) throws UserNotFoundException, ActiveUserException, GameAlreadyExistsException {
        System.out.println("Inside of create game");
        if (principal.getName().equals(user.getUsername())) {
            SingleplayerGame game = singleGameService.createGame(gameId, sessionId, user);
            simpMessagingTemplate.convertAndSend("/game/single/status/" + gameId, game);
        }
    }

    @MessageMapping("/start/single/{gameId}/{session}")
    public void startGame(@DestinationVariable(value = "gameId") String gameId, @Header("simpUser") UsernamePasswordAuthenticationToken principal) throws InvalidGameStateException, GameNotFoundException, UnsupportedGameTypeException {
        System.out.println("Entering start game controller");
        SingleplayerGame game = singleGameService.gameStart(gameId);
        simpMessagingTemplate.convertAndSend("/game/single/gameplay/" + gameId, game);
        simpMessagingTemplate.convertAndSend("/game/single/status/" + gameId, game);
    }

    @MessageMapping("/gameplay/single/{gameId}/{session}")
    public void gameplay(@Header("simpSessionId") String sessionId, @DestinationVariable(value = "gameId") String gameId, @Header("simpUser") UsernamePasswordAuthenticationToken principal, Character input) throws InvalidGameStateException, PlayerNotFoundException, GameNotFoundException, UnsupportedGameTypeException {
        SingleplayerGame game = singleGameService.gamePlay(sessionId, gameId, input);
        simpMessagingTemplate.convertAndSend("/game/single/gameplay/" + game.getGameId(), game);
        if (game.getStatus() == GameStatus.READY) {
            simpMessagingTemplate.convertAndSend("/game/single/status/" + gameId, game);
        }
        if (game.getPlayer().getEndTime() != 0) {
            simpMessagingTemplate.convertAndSend("/game/single/playerStatus/" + gameId + "/" + sessionId, 1);
        }
    }

    @MessageMapping("/end/single/{gameId}/{session}")
    public void endGame(@Header("simpSessionId") String sessionId, @DestinationVariable(value = "gameId") String gameId) throws InvalidGameStateException, GameNotFoundException, UnsupportedGameTypeException {
        SingleplayerGame game = singleGameService.startTimer(gameId);
        simpMessagingTemplate.convertAndSend("/game/single/status/" + gameId, game);
    }

    @MessageExceptionHandler
    @SendTo("/game/single/errors/{gameId}/{session}")
    public String handleException(Throwable exception, @DestinationVariable(value = "session") String session, @DestinationVariable(value = "gameId") String gameId) {
        return exception.getMessage();
    }
}
