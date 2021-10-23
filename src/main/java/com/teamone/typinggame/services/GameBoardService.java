package com.teamone.typinggame.services;

import com.teamone.typinggame.models.GameBoard;
import com.teamone.typinggame.models.Player;

public interface GameBoardService {
    GameBoard createGameBoard(String paragraph, Player player);
}
