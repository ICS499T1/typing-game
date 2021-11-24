package com.teamone.typinggame.services.game;

import com.teamone.typinggame.exceptions.*;
import com.teamone.typinggame.models.game.Game;
import com.teamone.typinggame.models.User;
import com.teamone.typinggame.models.game.GameInterface;

public interface GameService {
    String generateGameId();
    GameInterface createGame(String gameId, String sessionId, User user) throws UserNotFoundException, ActiveUserException, GameAlreadyExistsException;
    GameInterface gameStart(String gameId) throws GameNotFoundException, InvalidGameStateException, UnsupportedGameTypeException;
    GameInterface gamePlay(String sessionId, String gameId, Character input) throws GameNotFoundException, PlayerNotFoundException, InvalidGameStateException, UnsupportedGameTypeException;
    GameInterface gameEnd(String gameId) throws GameNotFoundException, InvalidGameStateException, UnsupportedGameTypeException;
    GameInterface removePlayer(String sessionId) throws GameNotFoundException, PlayerNotFoundException, UnsupportedGameTypeException;
}
