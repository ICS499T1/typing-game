package com.teamone.typinggame.services;

import com.teamone.typinggame.exceptions.*;
import com.teamone.typinggame.models.*;
import com.teamone.typinggame.repositories.UserRepository;
import com.teamone.typinggame.storage.ActiveUserStorage;
import com.teamone.typinggame.storage.GameStorage;
import com.teamone.typinggame.storage.PlayerStorage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

import static com.teamone.typinggame.models.GameStatus.*;

@Service
@RequiredArgsConstructor
public class GameServiceImpl implements GameService {
    private final GameStorage gameStorage = GameStorage.getInstance();
    private final ActiveUserStorage activeUserStorage = ActiveUserStorage.getInstance();
    private final PlayerStorage playerStorage = PlayerStorage.getInstance();
    private final UserRepository userRepository;

    @Override
    public synchronized Game createGame(String sessionId, User user) throws UserNotFoundException, ActiveUserException {
        if ((user = userRepository.findById(user.getUserID()).orElse(null)) == null) {
            throw new UserNotFoundException("User " + user.getUsername() + " does not exist.");
        }
        if (activeUserStorage.contains(user)) {
            throw new ActiveUserException("User " + user.getUsername() + " is already in a game.");
        }
        Game game = new Game(UUID.randomUUID().toString());
        Player player = new Player(user, game.getGameId());
        game.addPlayer(sessionId, player);
        activeUserStorage.addUser(user);
        playerStorage.addPlayer(sessionId, player);
        gameStorage.addGame(game);
        return game;
    }

    @Override
    public synchronized Game connectToGame(String sessionId, String gameId, User user) throws GameNotFoundException, UserNotFoundException, InvalidGameStateException, ActiveUserException {
        if (!gameStorage.contains(gameId)) {
            throw new GameNotFoundException("Game " + gameId + " does not exist.");
        }
        if (activeUserStorage.contains(user)) {
            throw new ActiveUserException("User " + user.getUsername() + " is already in a game.");
        }
        Game game = gameStorage.getGame(gameId);

        if (game.getPlayerCount() > 3) {
            throw new InvalidGameStateException("Game " + gameId + " is already full.");
        }
        if (game.getStatus() == IN_PROGRESS) {
            throw new InvalidGameStateException("Game " + gameId + " is in progress and cannot be joined.");
        }
        if ((user = userRepository.findById(user.getUserID()).orElse(null)) == null) {
            throw new UserNotFoundException("User " + user.getUsername() + " does not exist.");
        }
        Player player = new Player(user, gameId);
        activeUserStorage.addUser(user);
        playerStorage.addPlayer(sessionId, player);
        game.addPlayer(sessionId, player);
        game.setStatus(READY);
        gameStorage.addGame(game);
        return game;
    }

    @Override
    public synchronized Game gameStart(String gameId) throws GameNotFoundException, InvalidGameStateException {
        if (!gameStorage.contains(gameId)) {
            throw new GameNotFoundException("Game " + gameId + " does not exist.");
        }
        Game game = gameStorage.getGame(gameId);

        if (game.getStatus() != READY) {
            throw new InvalidGameStateException("Game " + gameId + " is not ready.");
        }
        game.reset();
        gameStorage.addGame(game);
        return game;
    }

    @Override
    public synchronized Game gameEnd(String gameId) throws GameNotFoundException, InvalidGameStateException {
        if (!gameStorage.contains(gameId)) {
            throw new GameNotFoundException("Game " + gameId + " does not exist.");
        }
        Game game = gameStorage.getGame(gameId);

        if (game.getStatus() != IN_PROGRESS) {
            throw new InvalidGameStateException("Game " + gameId + " cannot be ended.");
        }
        Integer playerCount = game.getPlayerCount();

        //TODO Add logic for processing user stats

        if (playerCount > 1) {
            game.setStatus(READY);
        } else {
            game.setStatus(WAITING_FOR_ANOTHER_PLAYER);
        }

        gameStorage.addGame(game);
        return game;
    }

    public synchronized void removePlayer(Player player) throws GameNotFoundException {
        String gameId = player.getGameId();
        if (!gameStorage.contains(gameId)) {
            throw new GameNotFoundException("Game " + gameId + " does not exist.");
        }
        Game game = gameStorage.getGame(gameId);

        //TODO Add logic for processing user stats

        game.removePlayer(player);
        activeUserStorage.removeUser(player.getUserID());

        if (game.getPlayerCount() < 1) {
            gameStorage.removeGame(game);
        } else if (game.getPlayerCount() == 1 && game.getStatus() == READY) {
            game.setStatus(WAITING_FOR_ANOTHER_PLAYER);
            gameStorage.addGame(game);
        }
    }

    public synchronized Game gamePlay(String sessionId, String gameId, Character input) throws GameNotFoundException, PlayerNotFoundException {
        if (!gameStorage.contains(gameId)) {
            throw new GameNotFoundException("Game " + gameId + " does not exist.");
        }
        Game game = gameStorage.getGame(gameId);
        if (!game.containsPlayer(sessionId)) {
            throw new PlayerNotFoundException("Player not found.");
        }
        Player player = game.getPlayer(sessionId);

        if (game.getGameText().charAt(player.getPosition()) == input) {
            player.incrementPosition();
        }
        return null;
    }


}
