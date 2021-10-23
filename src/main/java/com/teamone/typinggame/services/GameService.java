package com.teamone.typinggame.services;

import com.teamone.typinggame.models.Game;
import com.teamone.typinggame.models.User;

public interface GameService {
    Game createGame(User user);
    Game connectToGame(User user, String gameId);
    Game gameStart(String gameId);
}
