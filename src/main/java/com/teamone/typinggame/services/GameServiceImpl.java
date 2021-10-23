package com.teamone.typinggame.services;

import com.teamone.typinggame.models.*;
import com.teamone.typinggame.repositories.GameRepository;
import com.teamone.typinggame.repositories.PlayerRepository;
import com.teamone.typinggame.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import static com.teamone.typinggame.models.GameStatus.WAITING_FOR_ANOTHER_PLAYER;

@Service
@RequiredArgsConstructor
public class GameServiceImpl implements GameService {
    private final GameRepository gameRepository;
    private final UserRepository userRepository;
    private final PlayerRepository playerRepository;
    private final GameBoardService gameBoardService;

    @Override
    public Game createGame(User user) {
        Game game = new Game();
        Set<User> users = new HashSet<>();
        users.add(user);
        game.setUsers(users);
        game.setStatus(WAITING_FOR_ANOTHER_PLAYER);
        game.setGameId(UUID.randomUUID().toString());
        Player player = new Player(user);
        GameBoard gameBoard = gameBoardService.createGameBoard("Random paragraph!", player);
        game.setGameBoard(gameBoard);
        game = gameRepository.saveAndFlush(game);
        return game;
    }

    @Override
    public Game connectToGame(User user, String gameId) {
        Game game;
        if ((game = gameRepository.findByGameId(gameId)) == null) {
            System.out.println("Game does not exist.");
            return null;
        }

        if (game.getStatus() != WAITING_FOR_ANOTHER_PLAYER) {
            System.out.println("Game is not in a joinable state.");
            return null;
        }

        if ((user = userRepository.findById(user.getUserID()).orElse(null)) == null) {
            System.out.println("User does not exist.");
            return null;
        }

        Player player;
        if ((playerRepository.existsById(user.getUserID()))) {
            System.out.println("Player is already in a game.");
            return null;
        }
        player = new Player(user);

        game.getUsers().add(user);
        game.getGameBoard().getPlayers().add(player);
        game.setStatus(GameStatus.READY);
        game = gameRepository.saveAndFlush(game);

        return game;
    }

    @Override
    public Game gameStart(String gameId) {
        Game game;
        if ((game = gameRepository.findByGameId(gameId)) == null) {
            System.out.println("Game does not exist.");
            return null;
        }

        if (game.getStatus() != GameStatus.READY) {
            System.out.println("Game cannot be started.");
            return null;
        }



        return null;
    }


}
