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

    abstract GameInterface createGame(String gameId, String sessionId, User user) throws UserNotFoundException, ActiveUserException, GameAlreadyExistsException;
    abstract GameInterface gameStart(String gameId) throws GameNotFoundException, InvalidGameStateException, UnsupportedGameTypeException;
    abstract GameInterface gamePlay(String sessionId, String gameId, Character input) throws GameNotFoundException, PlayerNotFoundException, InvalidGameStateException, UnsupportedGameTypeException;
    abstract GameInterface startTimer(String gameId) throws GameNotFoundException, InvalidGameStateException, UnsupportedGameTypeException;
    abstract GameInterface removePlayer(String sessionId) throws GameNotFoundException, PlayerNotFoundException, UnsupportedGameTypeException;
}
