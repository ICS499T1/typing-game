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
    private final Object lockObject = new Object();

    @Override
    public String generateGameId() {
        return UUID.randomUUID().toString();
    }

    @Override
    public Game createGame(String gameId, String sessionId, User user) throws UserNotFoundException, ActiveUserException, GameAlreadyExistsException {
        String username = user.getUsername();

        if ((user = userRepository.findByUsername(username)) == null) {
            throw new UserNotFoundException("User " + username + " does not exist.");
        }

        Game game = new Game(gameId);
        Player player = new Player(user, game.getGameId());
        game.addPlayer(sessionId, player);

        synchronized (lockObject) {
            if (gameStorage.contains(gameId)) {
                throw new GameAlreadyExistsException("Game " + gameId + " already exists.");
            }

            if (activeUserStorage.contains(user)) {
                throw new ActiveUserException("User " + username + " is already in a game.");
            }

            // TODO find another solution for single player
            activeUserStorage.addUser(user);
            gameStorage.addGame(game);
            return game;
        }
    }

    @Override
    public Game connectToGame(String sessionId, String gameId, User user) throws GameNotFoundException, UserNotFoundException, InvalidGameStateException, ActiveUserException {
        if (!gameStorage.contains(gameId)) {
            throw new GameNotFoundException("Game " + gameId + " does not exist.");
        }
        String username = user.getUsername();
        if ((user = userRepository.findByUsername(username)) == null) {
            throw new UserNotFoundException("User " + username + " does not exist.");
        }

        Game game = gameStorage.getGame(gameId);
        Player player = new Player(user, gameId);


        synchronized (lockObject) {
            if (activeUserStorage.contains(user)) {
                throw new ActiveUserException("User " + user.getUsername() + " is already in a game.");
            }

            if (game.getPlayerCount() > 3) {
                throw new InvalidGameStateException("Game " + gameId + " is already full.");
            }
            if (game.getStatus() == IN_PROGRESS) {
                throw new InvalidGameStateException("Game " + gameId + " is in progress and cannot be joined.");
            }

            activeUserStorage.addUser(user);
            playerStorage.addPlayer(sessionId, player);
            game.addPlayer(sessionId, player);
            game.setStatus(READY);
        }

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
        game.setStatus(IN_PROGRESS);
        game.setStartTime(System.currentTimeMillis());
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

        Map<String, Player> players = game.getPlayers();



        Integer playerCount = game.getPlayerCount();

        //TODO Add logic for processing user stats


        game.reset();

        //TODO reset game here
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
        activeUserStorage.removeUser(player.getUsername());

        if (game.getPlayerCount() < 1) {
            gameStorage.removeGame(game);
        } else if (game.getPlayerCount() == 1 && game.getStatus() == READY) {
            game.setStatus(WAITING_FOR_ANOTHER_PLAYER);
            gameStorage.addGame(game);
        }
    }

    public Game gamePlay(String sessionId, String gameId, Character input) throws GameNotFoundException, PlayerNotFoundException, InvalidGameStateException {
        Game game = gameStorage.getGame(gameId);
        if (game == null) {
            throw new GameNotFoundException("Game " + gameId + " not found.");
        }
        Player player = game.getPlayer(sessionId);
        if (player == null) {
            throw new PlayerNotFoundException("Player " + sessionId + " not found.");
        }
        Integer position = player.getPosition();

        List<Character> gameText = game.getGameText();

        if (!player.getIncorrectCharacters().isEmpty()) {
            if (input == '\b') {
                player.removeIncorrectCharacter();
            } else {
                player.addIncorrectCharacter(input);
            }
        } else if (input == '\b') {
            return game;
        } else if (gameText.get(position) == input) {
            player.incrementPosition();
        } else {
            player.addFailedCharacter(gameText.get(position));
            player.addIncorrectCharacter(input);
        }

        // Getting player position again for updated position
        if (gameText.size() == player.getPosition()) {
            player.setEndTime(System.currentTimeMillis());
            if (game.getWinner() == null) {
                game.setWinner(player);
            } else if (game.getDoneCount() < game.getPlayerCount()) {
                game.incrementDoneCount();
            }
        }

        if (game.getDoneCount().equals(game.getPlayerCount())) {
            return gameEnd(game.getGameId());
        }
        return game;
    }


    public void startTimer(String gameId, String sessionId) throws GameNotFoundException, InvalidGameStateException {
        if (!gameStorage.contains(gameId)) {
            throw new GameNotFoundException("Game " + gameId + " does not exist.");
        }
        Game game = gameStorage.getGame(gameId);
        Player player = game.getPlayer(sessionId);
        if (game.getPlayer(sessionId).getPlayerNumber() != 1) {
            throw new InvalidGameStateException("Player " + player.getUsername() + " is not allowed to start timer.");
        }
    }
}
