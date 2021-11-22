package com.teamone.typinggame.services.game;

import com.teamone.typinggame.exceptions.*;
import com.teamone.typinggame.models.Game;
import com.teamone.typinggame.models.Player;
import com.teamone.typinggame.models.User;

public interface GameService {
    String generateGameId();
    Game createGame(String gameId, String sessionId, User user) throws UserNotFoundException, ActiveUserException, GameAlreadyExistsException;
    Game connectToGame(String sessionId, String gameId, User user) throws GameNotFoundException, UserNotFoundException, InvalidGameStateException, ActiveUserException;
    Game gameStart(String gameId) throws GameNotFoundException, InvalidGameStateException;
    Game gameEnd(String gameId) throws GameNotFoundException, InvalidGameStateException;
    void removePlayer(String sessionId) throws GameNotFoundException;
}
