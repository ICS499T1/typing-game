package com.teamone.typinggame.services;

import com.teamone.typinggame.models.GameBoard;
import com.teamone.typinggame.models.Player;
import com.teamone.typinggame.repositories.GameBoardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class GameBoardServiceImpl implements GameBoardService {
    private final GameBoardRepository gameBoardRepository;

    @Override
    public GameBoard createGameBoard(String paragraph, Player player) {
        GameBoard gameBoard = new GameBoard();
        gameBoard.setGameText(paragraph);
        Set<Player> players = new HashSet<>();
        players.add(player);
        gameBoard.setPlayers(players);
        return gameBoard;
    }
}
