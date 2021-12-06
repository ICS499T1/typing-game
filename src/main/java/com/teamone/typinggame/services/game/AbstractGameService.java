package com.teamone.typinggame.services.game;

import com.teamone.typinggame.exceptions.*;
import com.teamone.typinggame.models.User;
import com.teamone.typinggame.models.game.GameInterface;
import com.teamone.typinggame.repositories.UserRepository;
import com.teamone.typinggame.services.user.UserServiceImpl;
import com.teamone.typinggame.storage.ActiveUserStorage;
import com.teamone.typinggame.storage.GameStorage;
import com.teamone.typinggame.storage.PlayerStorage;
import lombok.RequiredArgsConstructor;

import java.util.UUID;

@RequiredArgsConstructor
public abstract class AbstractGameService {

    protected final GameStorage gameStorage = GameStorage.getInstance();
    protected final ActiveUserStorage activeUserStorage = ActiveUserStorage.getInstance();
    protected final PlayerStorage playerStorage = PlayerStorage.getInstance();
    protected final UserServiceImpl userService;
    protected final UserRepository userRepository;
    protected final Object lockObject = new Object();

    public String generateGameId() {
        return UUID.randomUUID().toString();
    }

    /**
     * Creates game for the user.
     *
     * @param gameId    - game id associated with the game
     * @param sessionId - player's session id
     * @param user      - user associated with the player
     * @return GameInterface - newly created game
     * @throws UserNotFoundException      when the user does not exist
     * @throws ActiveUserException        when the user is in another game
     * @throws GameAlreadyExistsException when the game already exists
     */
    abstract GameInterface createGame(String gameId, String sessionId, User user) throws UserNotFoundException, ActiveUserException, GameAlreadyExistsException;

    /**
     * Starts the game.
     *
     * @param gameId - game to be started
     * @return GameInterface - a started game
     * @throws GameNotFoundException        when the game does not exist
     * @throws InvalidGameStateException    when the game is not counting down
     * @throws UnsupportedGameTypeException when the user tries to start the wrong type of game
     */
    abstract GameInterface gameStart(String gameId) throws GameNotFoundException, InvalidGameStateException, UnsupportedGameTypeException;

    /**
     * Keeps track of players' positions and progress.
     *
     * @param sessionId - player's session id
     * @param gameId    - game id
     * @param input     - typed character
     * @return GameInterface - game with updated values
     * @throws GameNotFoundException        when the game does not exist
     * @throws PlayerNotFoundException      when the player does not exist
     * @throws InvalidGameStateException    when the player has already completed the game
     * @throws UnsupportedGameTypeException when the player tries to play the wrong type of game
     */
    abstract GameInterface gamePlay(String sessionId, String gameId, Character input) throws GameNotFoundException, PlayerNotFoundException, InvalidGameStateException, UnsupportedGameTypeException;

    /**
     * Starts the timer for the game.
     *
     * @param gameId - game id
     * @return GameInterface - a game in the countdown state
     * @throws GameNotFoundException        when the game does not exist
     * @throws InvalidGameStateException    when the timer cannot be started
     * @throws UnsupportedGameTypeException when the player tries to play the wrong type of game
     */
    abstract GameInterface startTimer(String gameId) throws GameNotFoundException, InvalidGameStateException, UnsupportedGameTypeException;

    /**
     * Removes player from the game.
     *
     * @param sessionId - player's id
     * @return GameInterface - a game without removed player
     * @throws GameNotFoundException        when the game does not exist
     * @throws PlayerNotFoundException      when the player does not exist
     * @throws UnsupportedGameTypeException when the player tries to play the wrong type of game
     */
    abstract GameInterface removePlayer(String sessionId) throws GameNotFoundException, PlayerNotFoundException, UnsupportedGameTypeException;
}
