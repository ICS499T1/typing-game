package com.teamone.typinggame.controllers;

import com.teamone.typinggame.exceptions.*;
import com.teamone.typinggame.models.game.MultiGame;
import com.teamone.typinggame.models.GameStatus;
import com.teamone.typinggame.models.User;
import com.teamone.typinggame.services.game.MultiGameServiceImpl;
import lombok.Data;
import org.springframework.messaging.handler.annotation.*;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Data
public class MultiGameController {
    private final MultiGameServiceImpl gameService;
    private final SimpMessagingTemplate simpMessagingTemplate;

    /**
     * Returns a newly generated game id for a new game.
     *
     * @return String - game id
     */
    @GetMapping("/get-game-id")
    public String getGameId() {
        return gameService.generateGameId();
    }

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
    @MessageMapping("/create/{gameId}/{session}")
    public void createGame(@DestinationVariable(value = "gameId") String gameId, @Header("simpSessionId") String sessionId, @Header("simpUser") UsernamePasswordAuthenticationToken principal, User user) throws UserNotFoundException, ActiveUserException, GameAlreadyExistsException {
        if (principal.getName().equals(user.getUsername())) {
            MultiGame multiGame = gameService.createGame(gameId, sessionId, user);
            simpMessagingTemplate.convertAndSend("/game/status/" + gameId, multiGame);
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
    @MessageMapping("/start/{gameId}/{session}")
    public void startGame(@DestinationVariable(value = "gameId") String gameId, @Header("simpUser") UsernamePasswordAuthenticationToken principal) throws InvalidGameStateException, GameNotFoundException, UnsupportedGameTypeException {
        // TODO add check for making sure game can only be started by player who created game (probably by checking if sessionId matches player1's sessionId)
        MultiGame multiGame = gameService.gameStart(gameId);
        simpMessagingTemplate.convertAndSend("/game/gameplay/" + gameId, multiGame);
        simpMessagingTemplate.convertAndSend("/game/status/" + gameId, multiGame);
    }

    /**
     * Allows for other users (not the creator) to join the game.
     *
     * @param sessionId - player's session id
     * @param gameId    - game id
     * @param principal - authorization token
     * @param user      - requesting user
     * @throws UserNotFoundException        when the user does not exist
     * @throws InvalidGameStateException    when the game is already full or in progress
     * @throws GameNotFoundException        when the game does not exist
     * @throws ActiveUserException          when the user is in another game
     * @throws UnsupportedGameTypeException when the user tries to access a multiplayer game as a single player game
     */
    @MessageMapping("/join/{gameId}/{session}")
    public void joinGame(@Header("simpSessionId") String sessionId, @DestinationVariable(value = "gameId") String gameId, @Header("simpUser") UsernamePasswordAuthenticationToken principal, User user) throws UserNotFoundException, InvalidGameStateException, GameNotFoundException, ActiveUserException, UnsupportedGameTypeException {
        if (principal.getName().equals(user.getUsername())) {
            MultiGame multiGame = gameService.connectToGame(sessionId, gameId, user);
            simpMessagingTemplate.convertAndSend("/game/status/" + gameId, multiGame);
        }
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
    @MessageMapping("/gameplay/{gameId}/{session}")
    public void gameplay(@Header("simpSessionId") String sessionId, @DestinationVariable(value = "gameId") String gameId, @Header("simpUser") UsernamePasswordAuthenticationToken principal, Character input) throws InvalidGameStateException, PlayerNotFoundException, GameNotFoundException, UnsupportedGameTypeException {
        MultiGame multiGame = gameService.gamePlay(sessionId, gameId, input);
        simpMessagingTemplate.convertAndSend("/game/gameplay/" + multiGame.getGameId(), multiGame);
        if (multiGame.getStatus() == GameStatus.READY || multiGame.getStatus() == GameStatus.WAITING_FOR_ANOTHER_PLAYER) {
            simpMessagingTemplate.convertAndSend("/game/status/" + gameId, multiGame);
        }
        if (multiGame.getPlayer(sessionId).getEndTime() != 0) {
            simpMessagingTemplate.convertAndSend("/game/playerStatus/" + gameId + "/" + sessionId, 1);
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
    @MessageMapping("/timer/{gameId}/{session}")
    public void startTimer(@DestinationVariable(value = "gameId") String gameId) throws InvalidGameStateException, GameNotFoundException, UnsupportedGameTypeException {
        // TODO add check for making sure timer can only be started by player who created game (probably by checking if sessionId matches player1's sessionId)
        MultiGame multiGame = gameService.startTimer(gameId);
        simpMessagingTemplate.convertAndSend("/game/status/" + gameId, multiGame);
    }

    /**
     * Removes the player once they send disconnect request.
     *
     * @param sessionId - player's session id
     * @throws PlayerNotFoundException      when the player does not exist
     * @throws UnsupportedGameTypeException when the user tries to access a multiplayer game as a single player game
     * @throws GameNotFoundException        when the game does not exist
     */
    public void removePlayer(String sessionId) throws PlayerNotFoundException, UnsupportedGameTypeException, GameNotFoundException {
        MultiGame multiGame = gameService.removePlayer(sessionId);
        simpMessagingTemplate.convertAndSend("/game/status/" + multiGame.getGameId(), multiGame);
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
    @SendTo("/game/errors/{gameId}/{session}")
    public String handleException(Throwable exception, @DestinationVariable(value = "session") String session, @DestinationVariable(value = "gameId") String gameId) {
        return exception.getMessage();
    }
}
