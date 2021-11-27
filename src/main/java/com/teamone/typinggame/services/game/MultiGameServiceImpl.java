package com.teamone.typinggame.services.game;

import com.teamone.typinggame.exceptions.*;
import com.teamone.typinggame.models.Player;
import com.teamone.typinggame.models.User;
import com.teamone.typinggame.models.game.MultiGame;
import com.teamone.typinggame.repositories.UserRepository;
import com.teamone.typinggame.services.user.UserServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.teamone.typinggame.models.GameStatus.*;

@Service
public class MultiGameServiceImpl extends AbstractGameService {

    public MultiGameServiceImpl(UserServiceImpl userService, UserRepository userRepository) {
        super(userService, userRepository);
    }

    @Override
    public synchronized MultiGame createGame(String gameId, String sessionId, User user) throws UserNotFoundException, ActiveUserException, GameAlreadyExistsException {
        String username = user.getUsername();

        if ((user = userRepository.findByUsername(username)) == null) {
            throw new UserNotFoundException("User " + username + " does not exist.");
        }

        if (activeUserStorage.contains(user)) {
            throw new ActiveUserException("User " + username + " is already in a game.");
        }

        MultiGame multiGame = new MultiGame(gameId);
        Player player = new Player(user, gameId);
        player.setPlayerNumber(1);
        multiGame.addPlayer(sessionId, player);

        if (gameStorage.contains(gameId)) {
            throw new GameAlreadyExistsException("Game " + gameId + " already exists.");
        }


        playerStorage.addPlayer(sessionId, player);
        activeUserStorage.addUser(user);
        gameStorage.addGame(multiGame);
        return multiGame;
    }

    /**
     * Connects other players to the game.
     *
     * @param sessionId - player's session id
     * @param gameId    - game id
     * @param user      - user associated with the player
     * @return MultiGame - updated game
     * @throws GameNotFoundException        when the game does not exist
     * @throws UserNotFoundException        when the user does not exist
     * @throws InvalidGameStateException    when the ga,e is already in progress or full
     * @throws ActiveUserException          when the user is in another game
     * @throws UnsupportedGameTypeException when the player tries to play the wrong type of game
     */
    public synchronized MultiGame connectToGame(String sessionId, String gameId, User user) throws GameNotFoundException, UserNotFoundException, InvalidGameStateException, ActiveUserException, UnsupportedGameTypeException {
        if (!gameStorage.contains(gameId)) {
            throw new GameNotFoundException("Game " + gameId + " does not exist.");
        }
        if (activeUserStorage.contains(user)) {
            throw new ActiveUserException("User " + user.getUsername() + " is already in a game.");
        }
        if (!(gameStorage.getGame(gameId) instanceof MultiGame)) {
            throw new UnsupportedGameTypeException("Cannot join single-player game {" + gameId + "}");
        }
        MultiGame multiGame = (MultiGame) gameStorage.getGame(gameId);
        if (multiGame.getPlayerCount() > 3) {
            throw new InvalidGameStateException("Game " + gameId + " is already full.");
        }
        if (multiGame.getStatus() == IN_PROGRESS || multiGame.getStatus() == COUNTDOWN) {
            throw new InvalidGameStateException("Game " + gameId + " is in progress and cannot be joined.");
        }

        String username = user.getUsername();
        if ((user = userRepository.findByUsername(username)) == null) {
            throw new UserNotFoundException("User " + username + " does not exist.");
        }


        Player player = new Player(user, gameId);
        player.setPlayerNumber(multiGame.getPlayerCount() + 1);
        activeUserStorage.addUser(user);
        playerStorage.addPlayer(sessionId, player);
        multiGame.addPlayer(sessionId, player);
        multiGame.setStatus(READY);
        return multiGame;
    }

    @Override
    public synchronized MultiGame gameStart(String gameId) throws GameNotFoundException, InvalidGameStateException, UnsupportedGameTypeException {
        if (!gameStorage.contains(gameId)) {
            throw new GameNotFoundException("Game " + gameId + " does not exist.");
        }
        if (!(gameStorage.getGame(gameId) instanceof MultiGame)) {
            throw new UnsupportedGameTypeException("{" + gameId + "}");
        }
        MultiGame multiGame = (MultiGame) gameStorage.getGame(gameId);

        if (multiGame.getStatus() != COUNTDOWN) {
            throw new InvalidGameStateException("Game " + gameId + " is not counting down.");
        }

        multiGame.setStatus(IN_PROGRESS);
        multiGame.setStartTime(System.currentTimeMillis());
        return multiGame;
    }

    @Override
    public synchronized MultiGame startTimer(String gameId) throws GameNotFoundException, InvalidGameStateException, UnsupportedGameTypeException {
        if (!gameStorage.contains(gameId)) {
            throw new GameNotFoundException("Game {" + gameId + "} does not exist.");
        }
        if (!(gameStorage.getGame(gameId) instanceof MultiGame)) {
            throw new UnsupportedGameTypeException("{" + gameId + "}");
        }
        MultiGame multiGame = (MultiGame) gameStorage.getGame(gameId);
        if (multiGame.getStatus() != READY) {
            throw new InvalidGameStateException("Timer for game {" + gameId + "} cannot be started.");
        }
        multiGame.reset();
        return multiGame;
    }

    @Override
    public synchronized MultiGame removePlayer(String sessionId) throws GameNotFoundException, PlayerNotFoundException, UnsupportedGameTypeException {
        Player player = playerStorage.getPlayer(sessionId);
        if (player == null)
            throw new PlayerNotFoundException("Player with session id {" + sessionId + "} could not be found.");
        String gameId = player.getGameId();

        if (!gameStorage.contains(gameId)) {
            throw new GameNotFoundException("Game " + gameId + " does not exist.");
        }
        if (!(gameStorage.getGame(gameId) instanceof MultiGame)) {
            throw new UnsupportedGameTypeException("{" + gameId + "}");
        }
        MultiGame multiGame = (MultiGame) gameStorage.getGame(gameId);
        if (multiGame.getStatus() == IN_PROGRESS) {
            player.setEndTime(System.currentTimeMillis());
            processUserStats(player, multiGame);

            if (player.getPosition() >= multiGame.getGameText().size() && multiGame.getDoneCount() > 0) {
                System.out.println("DECREMENTING");
                multiGame.decrementDoneCount();
            }
        }

        multiGame.removePlayer(sessionId);
        playerStorage.removePlayer(sessionId);
        activeUserStorage.removeUser(player.getUsername());

        if (multiGame.getPlayerCount() < 1) {
            gameStorage.removeGame(multiGame);
        } else if (multiGame.getPlayerCount() == 1 && multiGame.getStatus() == READY) {
            multiGame.setStatus(WAITING_FOR_ANOTHER_PLAYER);
            //gameStorage.addGame(game);
        }
        if (player.getPlayerNumber() == 1) {
            multiGame.reassignPlayers();
        }

        if (multiGame.getStatus() == COUNTDOWN) {
            if (multiGame.getPlayerCount() > 1) {
                return multiGame;
            } else {
                multiGame.reset();
                multiGame.setStatus(WAITING_FOR_ANOTHER_PLAYER);
            }
        }
        return multiGame;
    }

    @Override
    public MultiGame gamePlay(String sessionId, String gameId, Character input) throws GameNotFoundException, PlayerNotFoundException, InvalidGameStateException, UnsupportedGameTypeException {
        if (!(gameStorage.getGame(gameId) instanceof MultiGame)) {
            throw new UnsupportedGameTypeException("{" + gameId + "}");
        }
        MultiGame multiGame = (MultiGame) gameStorage.getGame(gameId);
        if (multiGame == null) {
            throw new GameNotFoundException("Game " + gameId + " not found.");
        }

        if (multiGame.getStatus() != IN_PROGRESS) {
            throw new InvalidGameStateException("Game " + gameId + " is not in progress.");
        }

        Player player = multiGame.getPlayer(sessionId);
        if (player == null) {
            throw new PlayerNotFoundException("Player " + sessionId + " not found.");
        }

        if (player.getEndTime() != 0) {
            throw new InvalidGameStateException("Player " + player.getUsername() + " is already done.");
        }

        Integer position = player.getPosition();

        List<Character> gameText = multiGame.getGameText();

        if (!player.getIncorrectCharacters().isEmpty()) {
            if (input == '\b') {
                player.removeIncorrectCharacter();
            } else {
                player.addIncorrectCharacter(input);
            }
        } else if (input == '\b') {
            return multiGame;
        } else if (gameText.get(position) == input) {
            player.incrementPosition();
        } else {
            player.addFailedCharacter(gameText.get(position));
            player.addIncorrectCharacter(input);
        }

        // Getting player position again for updated position
        if (gameText.size() == player.getPosition()) {
            player.setEndTime(System.currentTimeMillis());
            if (multiGame.getWinner() == null) {
                multiGame.setWinner(player);
            } else if (multiGame.getDoneCount() < multiGame.getPlayerCount()) {
                multiGame.incrementDoneCount();
            }
            processUserStats(player, multiGame);
        }

        if (multiGame.getDoneCount().equals(multiGame.getPlayerCount())) {
            if (multiGame.getPlayerCount() > 1) {
                multiGame.setStatus(READY);
            } else {
                multiGame.setStatus(WAITING_FOR_ANOTHER_PLAYER);
            }
        }
        return multiGame;
    }

    /**
     * Helper method to process user stats.
     *
     * @param player    - player for stats processing
     * @param multiGame - a game player participated in
     */
    private void processUserStats(Player player, MultiGame multiGame) {
        User user = activeUserStorage.getUser(player.getUsername());
        User updatedUser = player.calculateMultiplayerStats(user, multiGame.getGameText(), multiGame.getStartTime());
        userService.updateUserInfo(updatedUser);
    }
}
