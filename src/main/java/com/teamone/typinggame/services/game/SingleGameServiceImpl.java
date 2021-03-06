package com.teamone.typinggame.services.game;

import com.teamone.typinggame.exceptions.*;
import com.teamone.typinggame.models.Player;
import com.teamone.typinggame.models.User;
import com.teamone.typinggame.models.game.SingleGame;
import com.teamone.typinggame.repositories.UserRepository;
import com.teamone.typinggame.services.user.UserServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.teamone.typinggame.models.GameStatus.*;

@Service
public class SingleGameServiceImpl extends AbstractGameService {

    public SingleGameServiceImpl(UserServiceImpl userService, UserRepository userRepository) {
        super(userService, userRepository);
    }

    @Override
    public synchronized SingleGame createGame(String gameId, String sessionId, User user) throws UserNotFoundException, ActiveUserException, GameAlreadyExistsException {
        String username = user.getUsername();
        if ((user = userRepository.findByUsername(username)) == null) {
            throw new UserNotFoundException("User " + username + " does not exist.");
        }

        if (activeUserStorage.contains(user)) {
            throw new ActiveUserException("User " + username + " is already in a game.");
        }

        Player player = new Player(user, gameId);
        player.setPlayerNumber(1);
        SingleGame game = new SingleGame(gameId, player);

        if (gameStorage.contains(gameId)) {
            throw new GameAlreadyExistsException("Game " + gameId + " already exists.");
        }

        playerStorage.addPlayer(sessionId, player);
        activeUserStorage.addUser(user);
        gameStorage.addGame(game);
        return game;
    }

    @Override
    public synchronized SingleGame gameStart(String gameId) throws GameNotFoundException, InvalidGameStateException, UnsupportedGameTypeException {
        if (!gameStorage.contains(gameId)) {
            throw new GameNotFoundException("Game " + gameId + " does not exist.");
        }

        if (!(gameStorage.getGame(gameId) instanceof SingleGame)) {
            throw new UnsupportedGameTypeException("Cannot start a single-player game {" + gameId + "} when it's a multi-player");
        }
        SingleGame game = (SingleGame) gameStorage.getGame(gameId);

        if (game.getStatus() == IN_PROGRESS) {
            throw new InvalidGameStateException("Game " + gameId + " is in progress.");
        }

        game.setStatus(IN_PROGRESS);
        game.setStartTime(System.currentTimeMillis());
        return game;
    }

    @Override
    public synchronized SingleGame startTimer(String gameId) throws GameNotFoundException, InvalidGameStateException, UnsupportedGameTypeException {
        if (!gameStorage.contains(gameId)) {
            throw new GameNotFoundException("Game " + gameId + " does not exist.");
        }
        if (!(gameStorage.getGame(gameId) instanceof SingleGame)) {
            throw new UnsupportedGameTypeException("{" + gameId + "}");
        }
        SingleGame game = (SingleGame) gameStorage.getGame(gameId);
        if (game.getStatus() != READY) {
            throw new InvalidGameStateException("Game " + gameId + " cannot be ended.");
        }
        game.reset();
        return game;
    }

    @Override
    public synchronized SingleGame removePlayer(String sessionId) throws GameNotFoundException, PlayerNotFoundException, UnsupportedGameTypeException {
        Player player = playerStorage.getPlayer(sessionId);
        if (player == null) {
            throw new PlayerNotFoundException("Player with session id {" + sessionId + "} could not be found.");
        }
        String gameId = player.getGameId();

        if (!gameStorage.contains(gameId)) {
            throw new GameNotFoundException("Game " + gameId + " does not exist.");
        }
        if (!(gameStorage.getGame(gameId) instanceof SingleGame)) {
            throw new UnsupportedGameTypeException("{" + gameId + "}");
        }
        SingleGame game = (SingleGame) gameStorage.getGame(gameId);
        if (game.getStatus() == IN_PROGRESS) {
            player.setEndTime(System.currentTimeMillis());
            processUserStats(player, game);
        }
        gameStorage.removeGame(game);
        playerStorage.removePlayer(sessionId);
        activeUserStorage.removeUser(player.getUsername());
        return null;
    }

    @Override
    public SingleGame gamePlay(String sessionId, String gameId, Character input) throws GameNotFoundException, InvalidGameStateException, UnsupportedGameTypeException {
        if (!(gameStorage.getGame(gameId) instanceof SingleGame)) {
            throw new UnsupportedGameTypeException("{" + gameId + "}");
        }
        SingleGame game = (SingleGame) gameStorage.getGame(gameId);
        if (game == null) {
            throw new GameNotFoundException("Game " + gameId + " not found.");
        }

        if (game.getStatus() != IN_PROGRESS) {
            throw new InvalidGameStateException("Game " + gameId + " is not in progress.");
        }

        Player player = game.getPlayer();

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
            game.incrementDoneCount();
            processUserStats(player, game);
            game.setStatus(READY);
        }
        return game;
    }

    /**
     * Helper method to process user stats.
     *
     * @param player - player for stats processing
     * @param game   - a game player participated in
     */
    private void processUserStats(Player player, SingleGame game) {
        User user = activeUserStorage.getUser(player.getUsername());
        User updatedUser = player.calculateSinglePlayerStats(user, game.getGameText(), game.getStartTime());
        userService.updateUserInfo(updatedUser);
    }
}
