package com.teamone.typinggame.services.game;

import com.teamone.typinggame.exceptions.*;
import com.teamone.typinggame.models.Player;
import com.teamone.typinggame.models.User;
import com.teamone.typinggame.models.game.Game;
import com.teamone.typinggame.repositories.UserRepository;
import com.teamone.typinggame.services.user.UserServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.teamone.typinggame.models.GameStatus.*;

@Service
public class GameServiceImpl extends AbstractGameService {

    public GameServiceImpl(UserServiceImpl userService, UserRepository userRepository) {
        super(userService, userRepository);
    }

    @Override
    public synchronized Game createGame(String gameId, String sessionId, User user) throws UserNotFoundException, ActiveUserException, GameAlreadyExistsException {
        String username = user.getUsername();

        if ((user = userRepository.findByUsername(username)) == null) {
            throw new UserNotFoundException("User " + username + " does not exist.");
        }

        if (activeUserStorage.contains(user)) {
            throw new ActiveUserException("User " + username + " is already in a game.");
        }

        Game game = new Game(gameId);
        Player player = new Player(user, gameId);
        player.setPlayerNumber(1);
        game.addPlayer(sessionId, player);

        if (gameStorage.contains(gameId)) {
            throw new GameAlreadyExistsException("Game " + gameId + " already exists.");
        }


        playerStorage.addPlayer(sessionId, player);
        activeUserStorage.addUser(user);
        gameStorage.addGame(game);
        return game;
    }

    public synchronized Game connectToGame(String sessionId, String gameId, User user) throws GameNotFoundException, UserNotFoundException, InvalidGameStateException, ActiveUserException, UnsupportedGameTypeException {
        if (!gameStorage.contains(gameId)) {
            throw new GameNotFoundException("Game " + gameId + " does not exist.");
        }
        if (activeUserStorage.contains(user)) {
            throw new ActiveUserException("User " + user.getUsername() + " is already in a game.");
        }
        if (!(gameStorage.getGame(gameId) instanceof Game)) {
            throw new UnsupportedGameTypeException("Cannot join single-player game {" + gameId + "}");
        }
        Game game = (Game) gameStorage.getGame(gameId);
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
    public synchronized Game gameStart(String gameId) throws GameNotFoundException, InvalidGameStateException, UnsupportedGameTypeException {
        if (!gameStorage.contains(gameId)) {
            throw new GameNotFoundException("Game " + gameId + " does not exist.");
        }
        if (!(gameStorage.getGame(gameId) instanceof Game)) {
            throw new UnsupportedGameTypeException("{" + gameId + "}");
        }
        Game game = (Game) gameStorage.getGame(gameId);

        if (game.getStatus() != COUNTDOWN) {
            throw new InvalidGameStateException("Game " + gameId + " is not counting down.");
        }

        game.setStatus(IN_PROGRESS);
        game.setStartTime(System.currentTimeMillis());
        return game;
    }

    @Override
    public synchronized Game startTimer(String gameId) throws GameNotFoundException, InvalidGameStateException, UnsupportedGameTypeException {
        if (!gameStorage.contains(gameId)) {
            throw new GameNotFoundException("Game " + gameId + " does not exist.");
        }
        if (!(gameStorage.getGame(gameId) instanceof Game)) {
            throw new UnsupportedGameTypeException("{" + gameId + "}");
        }
        Game game = (Game) gameStorage.getGame(gameId);
        if (game.getStatus() != READY) {
            throw new InvalidGameStateException("Game " + gameId + " cannot be ended.");
        }
        game.reset();
        return game;
    }

    public synchronized Game removePlayer(String sessionId) throws GameNotFoundException, PlayerNotFoundException, UnsupportedGameTypeException {
        Player player = playerStorage.getPlayer(sessionId);
        if (player == null)
            throw new PlayerNotFoundException("Player with session id {" + sessionId + "} could not be found.");
        String gameId = player.getGameId();

        if (!gameStorage.contains(gameId)) {
            throw new GameNotFoundException("Game " + gameId + " does not exist.");
        }
        if (!(gameStorage.getGame(gameId) instanceof Game)) {
            throw new UnsupportedGameTypeException("{" + gameId + "}");
        }
        Game game = (Game) gameStorage.getGame(gameId);
        if (game.getStatus() == IN_PROGRESS) {
            player.setEndTime(System.currentTimeMillis());
            processUserStats(player, game);
        }

        if (player.getPosition() >= game.getGameText().size() && game.getDoneCount() > 0) {
            System.out.println("DECREMENTING");
            game.decrementDoneCount();
        }

        game.removePlayer(sessionId);
        playerStorage.removePlayer(sessionId);
        activeUserStorage.removeUser(player.getUsername());

        if (game.getPlayerCount() < 1) {
            gameStorage.removeGame(game);
        } else if (game.getPlayerCount() == 1 && game.getStatus() == READY) {
            game.setStatus(WAITING_FOR_ANOTHER_PLAYER);
            //gameStorage.addGame(game);
        }
        // TODO: test if the logic for reassigning players works
        if (player.getPlayerNumber() == 1) {
            game.reassignPlayers();
        }

        if (game.getStatus() == COUNTDOWN || game.getDoneCount() == game.getPlayerCount()) {
            if (game.getStatus() == COUNTDOWN) {
                game.reset();
            }
            if (game.getPlayerCount() > 1) {
                game.setStatus(READY);
            } else {
                game.setStatus(WAITING_FOR_ANOTHER_PLAYER);
            }
        }
        // TODO return the modified game on the /status channel so all other players can update the player being removed and new host being set
        return game;
    }

    @Override
    public Game gamePlay(String sessionId, String gameId, Character input) throws GameNotFoundException, PlayerNotFoundException, InvalidGameStateException, UnsupportedGameTypeException {
        if (!(gameStorage.getGame(gameId) instanceof Game)) {
            throw new UnsupportedGameTypeException("{" + gameId + "}");
        }
        Game game = (Game) gameStorage.getGame(gameId);
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
            if (game.getPlayerCount() > 1) {
                game.setStatus(READY);
            } else {
                game.setStatus(WAITING_FOR_ANOTHER_PLAYER);
            }
        }
        return game;
    }

//    public synchronized Game startTimer(String gameId, String sessionId) throws GameNotFoundException, InvalidGameStateException, UnsupportedGameTypeException {
//        if (!gameStorage.contains(gameId)) {
//            throw new GameNotFoundException("Game " + gameId + " does not exist.");
//        }
//        if (!(gameStorage.getGame(gameId) instanceof Game)) {
//            throw new UnsupportedGameTypeException("{" + gameId + "}");
//        }
//        Game game = (Game) gameStorage.getGame(gameId);
//
//        if (game.getStatus() == IN_PROGRESS || game.getStatus() == WAITING_FOR_ANOTHER_PLAYER) {
//            throw new InvalidGameStateException("Game " + gameId + " cannot be started.");
//        }
//        Player player = game.getPlayer(sessionId);
//        if (game.getPlayer(sessionId).getPlayerNumber() != 1) {
//            throw new InvalidGameStateException("Player " + player.getUsername() + " is not allowed to start timer.");
//        }
//        game.setStatus(COUNTDOWN);
//        return game;
//    }

    private void processUserStats(Player player, Game game) {
        User user = activeUserStorage.getUser(player.getUsername());
        User updatedUser = player.calculateMultiplayerStats(user, game.getGameText(), game.getStartTime());
        userService.updateUserInfo(updatedUser);
    }
}
