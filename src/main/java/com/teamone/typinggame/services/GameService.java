package com.teamone.typinggame.services;

import com.teamone.typinggame.exceptions.GameNotFoundException;
import com.teamone.typinggame.exceptions.InvalidGameStateException;
import com.teamone.typinggame.exceptions.ActiveUserException;
import com.teamone.typinggame.exceptions.UserNotFoundException;
import com.teamone.typinggame.models.Game;
import com.teamone.typinggame.models.Player;
import com.teamone.typinggame.models.User;

public interface GameService {
    Game createGame(String sessionId, User user) throws UserNotFoundException, ActiveUserException;
    Game connectToGame(String sessionId, String gameId, User user) throws GameNotFoundException, UserNotFoundException, InvalidGameStateException, ActiveUserException;
    Game gameStart(String gameId) throws GameNotFoundException, InvalidGameStateException;
    Game gameEnd(String gameId) throws GameNotFoundException, InvalidGameStateException;
    void removePlayer(Player player) throws GameNotFoundException;
}
