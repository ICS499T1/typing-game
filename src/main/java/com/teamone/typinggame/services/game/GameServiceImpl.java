package com.teamone.typinggame.services.game;

import com.teamone.typinggame.exceptions.*;
import com.teamone.typinggame.models.*;
import com.teamone.typinggame.repositories.UserRepository;
import com.teamone.typinggame.services.user.KeyStatsServiceImpl;
import com.teamone.typinggame.services.user.StatsServiceImpl;
import com.teamone.typinggame.services.user.UserServiceImpl;
import com.teamone.typinggame.storage.ActiveUserStorage;
import com.teamone.typinggame.storage.GameStorage;
import com.teamone.typinggame.storage.PlayerStorage;
import lombok.RequiredArgsConstructor;
import org.hibernate.query.criteria.internal.expression.function.AggregationFunction;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

import static com.teamone.typinggame.models.GameStatus.*;

@Service
@RequiredArgsConstructor
public class GameServiceImpl implements GameService {
    private final GameStorage gameStorage = GameStorage.getInstance();
    private final ActiveUserStorage activeUserStorage = ActiveUserStorage.getInstance();
    private final PlayerStorage playerStorage = PlayerStorage.getInstance();
    private final UserServiceImpl userService;
    private final StatsServiceImpl statsService;
    private final KeyStatsServiceImpl keyStatsService;
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
        player.setPlayerNumber(1);
        game.addPlayer(sessionId, player);
        playerStorage.addPlayer(sessionId, player);

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
        if (game.getStatus() == IN_PROGRESS || game.getStatus() == COUNTDOWN) {
            throw new InvalidGameStateException("Game " + gameId + " is in progress and cannot be joined.");
        }

        String username = user.getUsername();
        if ((user = userRepository.findByUsername(username)) == null) {
            throw new UserNotFoundException("User " + username + " does not exist.");
        }


        Player player = new Player(user, gameId);
        player.setPlayerNumber(game.getPlayerCount() + 1);
        activeUserStorage.addUser(user);
        playerStorage.addPlayer(sessionId, player);
        game.addPlayer(sessionId, player);
        game.setStatus(READY);
        return game;
    }

    @Override
    public synchronized Game gameStart(String gameId) throws GameNotFoundException, InvalidGameStateException {
        if (!gameStorage.contains(gameId)) {
            throw new GameNotFoundException("Game " + gameId + " does not exist.");
        }
        Game game = gameStorage.getGame(gameId);
        System.out.println("Trying to start game: " + game.getStatus());

        if (game.getStatus() != COUNTDOWN) {
            System.out.println("Trying to start game (inside if): " + game.getStatus());
            throw new InvalidGameStateException("Game " + gameId + " is not counting down.");
        }

        game.setStatus(IN_PROGRESS);
        System.out.println("After setting new status: " + game.getStatus());
        game.setStartTime(System.currentTimeMillis());
        return game;
    }

    @Override
    public synchronized Game gameEnd(String gameId) throws GameNotFoundException, InvalidGameStateException {
        if (!gameStorage.contains(gameId)) {
            throw new GameNotFoundException("Game " + gameId + " does not exist.");
        }
        Game game = gameStorage.getGame(gameId);
        if (game.getStatus() != COMPLETED) {
            throw new InvalidGameStateException("Game " + gameId + " cannot be ended.");
        }
        game.reset();
        return game;
    }

    public synchronized void removePlayer(String sessionId) throws GameNotFoundException {
        Player player = playerStorage.getPlayer(sessionId);
        System.out.println(PlayerStorage.getPlayers().size());
        PlayerStorage.getPlayers().forEach((session, pl) -> System.out.println("Session: "+ session + " Player: " + pl));
        String gameId = player.getGameId();

        if (!gameStorage.contains(gameId)) {
            throw new GameNotFoundException("Game " + gameId + " does not exist.");
        }
        Game game = gameStorage.getGame(gameId);

        if (game.getStatus() == IN_PROGRESS) {
            player.setEndTime(System.currentTimeMillis());
            processUserStats(player, game);
        }

        game.removePlayer(player);
        activeUserStorage.removeUser(player.getUsername());

        // Reassign player numbers in case the host has left
        if (player.getPlayerNumber() == 1 && game.getPlayerCount() > 1) {
            AtomicInteger playerNum = new AtomicInteger(0);
            Map<String, Player> playerMap = game.getPlayers();
            playerMap.forEach((sessionIdObj, playerObj) -> playerObj.setPlayerNumber(playerNum.incrementAndGet()));
        }

        if (game.getPlayerCount() < 1) {
            gameStorage.removeGame(game);
        } else if (game.getPlayerCount() == 1 && game.getStatus() == READY) {
            game.setStatus(WAITING_FOR_ANOTHER_PLAYER);
            gameStorage.addGame(game);
        }
        // TODO return the modified game on the /status channel so all other players can update the player being removed and new host being set
    }

    public Game gamePlay(String sessionId, String gameId, Character input) throws GameNotFoundException, PlayerNotFoundException, InvalidGameStateException, UserNotFoundException {
        Game game = gameStorage.getGame(gameId);
        if (game == null) {
            throw new GameNotFoundException("Game " + gameId + " not found.");
        }

        if (game.getStatus() != IN_PROGRESS) {
            throw new InvalidGameStateException("Game " + gameId + " is not in progress.");
        }

        Player player = game.getPlayer(sessionId);
        if (player == null) {
            throw new PlayerNotFoundException("Player " + sessionId + " not found.");
        }

        if (player.getEndTime() != 0) {
            throw new InvalidGameStateException("Player " + player.getUsername() + " is already done.");
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
            processUserStats(player, game);
        }

        if (game.getDoneCount().equals(game.getPlayerCount())) {
            game.setStatus(COMPLETED);
        }
        return game;
    }

    public synchronized Game startTimer(String gameId, String sessionId) throws GameNotFoundException, InvalidGameStateException {
        if (!gameStorage.contains(gameId)) {
            throw new GameNotFoundException("Game " + gameId + " does not exist.");
        }
        Game game = gameStorage.getGame(gameId);

        if (game.getStatus() == IN_PROGRESS || game.getStatus() == WAITING_FOR_ANOTHER_PLAYER) {
            throw new InvalidGameStateException("Game " + gameId + " cannot be started.");
        }
        Player player = game.getPlayer(sessionId);
        if (game.getPlayer(sessionId).getPlayerNumber() != 1) {
            throw new InvalidGameStateException("Player " + player.getUsername() + " is not allowed to start timer.");
        }
        game.setStatus(COUNTDOWN);
        return game;
    }

    private void processUserStats(Player player, Game game) {
        User user = activeUserStorage.getUser(player.getUsername());
        User updatedUser = player.calculateStats(user, game.getGameText(), game.getStartTime());
        userService.updateUserInfo(updatedUser);
    }
}
