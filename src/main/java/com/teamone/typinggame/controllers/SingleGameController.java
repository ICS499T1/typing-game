package com.teamone.typinggame.controllers;

import com.teamone.typinggame.exceptions.*;
import com.teamone.typinggame.models.GameStatus;
import com.teamone.typinggame.models.User;
import com.teamone.typinggame.models.game.SingleGame;
import com.teamone.typinggame.services.game.SingleGameServiceImpl;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.*;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.RestController;

@Data
@RestController
public class SingleGameController {

    private final SingleGameServiceImpl singleGameService;
    private final SimpMessagingTemplate simpMessagingTemplate;

    /**
     * Creates a game after receiving a request from the front-and.
     *
     * @param gameId    - game id for game creation
     * @param sessionId - player's session id
     * @param principal - authentication object
     * @param user      - requesting user
     * @throws UserNotFoundException      when the user is not found
     * @throws ActiveUserException        when the user is in another game
     * @throws GameAlreadyExistsException when the game with that game id has already been created
     */
    @MessageMapping("/create/single/{gameId}/{session}")
    public void createGame(@DestinationVariable(value = "gameId") String gameId, @Header("simpSessionId") String sessionId, @Header("simpUser") UsernamePasswordAuthenticationToken principal, User user) throws UserNotFoundException, ActiveUserException, GameAlreadyExistsException {
        System.out.println("Inside of create game");
        if (principal.getName().equals(user.getUsername())) {
            SingleGame game = singleGameService.createGame(gameId, sessionId, user);
            simpMessagingTemplate.convertAndSend("/game/single/status/" + gameId, game);
        }
    }

    /**
     * Starts the game after the countdown is over.
     *
     * @param gameId    - game id
     * @param principal - authorization object
     * @throws InvalidGameStateException    when the game is not ready to be started
     * @throws GameNotFoundException        when the game does not exist
     * @throws UnsupportedGameTypeException when the user tries to access a multiplayer game as a single player game
     */
    @MessageMapping("/start/single/{gameId}/{session}")
    public void startGame(@DestinationVariable(value = "gameId") String gameId, @Header("simpUser") UsernamePasswordAuthenticationToken principal) throws InvalidGameStateException, GameNotFoundException, UnsupportedGameTypeException {
        SingleGame game = singleGameService.gameStart(gameId);
        simpMessagingTemplate.convertAndSend("/game/single/gameplay/" + gameId, game);
        simpMessagingTemplate.convertAndSend("/game/single/status/" + gameId, game);
    }

    /**
     * Processes data that is coming in during the gameplay.
     *
     * @param sessionId - player's session id
     * @param gameId    - game id
     * @param principal - authorization object
     * @param input     - incoming character
     * @throws InvalidGameStateException    when the game is not in progress or the player is already done
     * @throws PlayerNotFoundException      when the player does not exist
     * @throws GameNotFoundException        when the game does not exist
     * @throws UnsupportedGameTypeException when the user tries to access a multiplayer game as a single player game
     */
    @MessageMapping("/gameplay/single/{gameId}/{session}")
    public synchronized void gameplay(@Header("simpSessionId") String sessionId, @DestinationVariable(value = "gameId") String gameId, @Header("simpUser") UsernamePasswordAuthenticationToken principal, Character input) throws InvalidGameStateException, PlayerNotFoundException, GameNotFoundException, UnsupportedGameTypeException {
        System.out.println(input.toString());
        SingleGame game = singleGameService.gamePlay(sessionId, gameId, input);
        simpMessagingTemplate.convertAndSend("/game/single/gameplay/" + game.getGameId(), game);
        if (game.getStatus() == GameStatus.READY) {
            simpMessagingTemplate.convertAndSend("/game/single/status/" + gameId, game);
        }
    }

    /**
     * Starts game timer.
     *
     * @param gameId - game id
     * @throws InvalidGameStateException    when the game is not ready to be started
     * @throws GameNotFoundException        when the game does not exist
     * @throws UnsupportedGameTypeException when the user tries to access a multiplayer game as a single player game
     */
    @MessageMapping("/timer/single/{gameId}/{session}")
    public void startTimer(@Header("simpSessionId") String sessionId, @DestinationVariable(value = "gameId") String gameId) throws InvalidGameStateException, GameNotFoundException, UnsupportedGameTypeException {
        SingleGame game = singleGameService.startTimer(gameId);
        simpMessagingTemplate.convertAndSend("/game/single/status/" + gameId, game);
    }

    /**
     * Handles exceptions and forwards them to the front-end.
     *
     * @param exception - exception being thrown
     * @param session   - player's session id
     * @param gameId    - game id
     * @return String - exception message
     */
    @MessageExceptionHandler
    @SendTo("/game/single/errors/{gameId}/{session}")
    public String handleException(Throwable exception, @DestinationVariable(value = "session") String session, @DestinationVariable(value = "gameId") String gameId) {
        return exception.getMessage();
    }
}
