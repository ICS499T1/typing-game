package com.teamone.typinggame.services.game;

import com.teamone.typinggame.exceptions.*;
import com.teamone.typinggame.models.*;
import com.teamone.typinggame.repositories.UserRepository;
import com.teamone.typinggame.storage.ActiveUserStorage;
import com.teamone.typinggame.storage.GameStorage;
import com.teamone.typinggame.storage.PlayerStorage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
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
    public String generateGameId() {
        return UUID.randomUUID().toString();
    }

    @Override
    public synchronized Game createGame(String gameId, String sessionId, User user) throws UserNotFoundException, ActiveUserException, GameAlreadyExistsException {
/*        if ((user = userRepository.findById(user.getUserID()).orElse(null)) == null) {
            throw new UserNotFoundException("User " + user.getUsername() + " does not exist.");
        }*/
        String username = user.getUsername();
        if (gameStorage.contains(gameId)) {
            throw new GameAlreadyExistsException("Game " + gameId + " already exists.");
        }
        if ((user = userRepository.findByUsername(username)) == null) {
            throw new UserNotFoundException("User " + username + " does not exist.");
        }
        if (activeUserStorage.contains(user)) {
            throw new ActiveUserException("User " + username + " is already in a game.");
        }
        // System.out.println("GameServiceImpl: " + user);
        Game game = new Game(gameId);
        Player player = new Player(user, game.getGameId());
        game.addPlayer(sessionId, player);
        // TODO find another solution for single player
        activeUserStorage.addUser(user);
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
/*        if ((user = userRepository.findById(user.getUserID()).orElse(null)) == null) {
            throw new UserNotFoundException("User " + user.getUsername() + " does not exist.");
        }*/
        String username = user.getUsername();
        if ((user = userRepository.findByUsername(username)) == null) {
            throw new UserNotFoundException("User " + username + " does not exist.");
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

        //TODO change it to playerCount>1 once multiplayer ready
        if (playerCount > 0) {
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
        // TODO Add logic for processing players that haven't finished the text or finished partially

        game.removePlayer(player);
        activeUserStorage.removeUser(player.getUserID());

        if (game.getPlayerCount() < 1) {
            gameStorage.removeGame(game);
        } else if (game.getPlayerCount() == 1 && game.getStatus() == READY) {
            game.setStatus(WAITING_FOR_ANOTHER_PLAYER);
            gameStorage.addGame(game);
        }
    }

    public synchronized Game gamePlay(String sessionId, String gameId, Character input) throws GameNotFoundException, PlayerNotFoundException, InvalidGameStateException {
        if (!gameStorage.contains(gameId)) {
            throw new GameNotFoundException("Game " + gameId + " does not exist.");
        }
        Game game = gameStorage.getGame(gameId);
        if (!game.containsPlayer(sessionId)) {
            throw new PlayerNotFoundException("Player not found.");
        }
        Player player = game.getPlayer(sessionId);

        List<Character> gameText = game.getGameText();

        if (!player.getIncorrectCharacters().isEmpty()) {
            if (input == '\b') {
                player.removeIncorrectCharacter();
            } else {
                player.addIncorrectCharacter(input);
            }
        } else if (input == '\b') {
            return game;
        } else if (gameText.get(player.getPosition()) == input) {
            player.incrementPosition();
        } else {
            player.addFailedCharacter(gameText.get(player.getPosition()));
            player.addIncorrectCharacter(input);
        }

        if (gameText.size() == player.getPosition()) {
            player.setEndTime(System.currentTimeMillis());
            if (game.getWinner() == null) {
                player.setWinner(true);
                game.setWinner(player);
            }
            if (game.getDoneCount() < game.getPlayerCount()) {
                game.incrementDoneCount();
            }
        }

        game.updatePlayer(sessionId, player);
        if (game.getDoneCount().equals(game.getPlayerCount())) {
            return gameEnd(game.getGameId());
        }
        gameStorage.addGame(game);
        return game;
    }


}
