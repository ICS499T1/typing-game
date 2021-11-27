package com.teamone.typinggame.models;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.*;

@Data
@Component
@NoArgsConstructor
public class Player {

    private String username;

    private Integer position;

    private List<Character> failedCharacters;

    private Stack<Character> incorrectCharacters;

    private String gameId;

    private Boolean winner;

    private Long endTime;

    private Integer playerNumber;

    /**
     * Basic constructor to initialize a player.
     *
     * @param user   - user about to play
     * @param gameId - game id
     */
    public Player(User user, String gameId) {
        this.username = user.getUsername();
        this.winner = false;
        this.endTime = 0L;
        this.gameId = gameId;
        this.position = 0;
        this.incorrectCharacters = new Stack<>();
        this.failedCharacters = new ArrayList<>();
    }

    @Override
    public boolean equals(Object o) {
        return (o instanceof Player && ((Player) o).getUsername().equals(this.username));
    }

    @Override
    public int hashCode() {
        if (username == null) {
            return 0;
        }
        return username.hashCode();
    }

    /**
     * Increments position of the player.
     */
    public void incrementPosition() {
        position++;
    }

    /**
     * Adds characters player made mistakes in to the list.
     *
     * @param character - failed character
     */
    public void addFailedCharacter(Character character) {
        failedCharacters.add(character);
    }

    /**
     * Adds incorrect characters to the stack.
     *
     * @param character - incorrect character
     */
    public void addIncorrectCharacter(Character character) {
        incorrectCharacters.add(character);
    }

    /**
     * Removes latest incorrect character from the stack.
     */
    public void removeIncorrectCharacter() {
        incorrectCharacters.pop();
    }

    /**
     * Reset all values for the player.
     */
    public void reset() {
        position = 0;
        failedCharacters = new ArrayList<>();
        incorrectCharacters = new Stack<>();
        winner = false;
        endTime = 0L;
    }

    /**
     * Calculates stats for a player for a single player game.
     *
     * @param user      - user associated with the player
     * @param gameText  - text provided by the game
     * @param startTime - the time the user started playing
     * @return User with updated stats and keyStats
     */
    public User calculateSinglePlayerStats(User user, List<Character> gameText, Long startTime) {
        List<Character> completedText = gameText.subList(0, position);
        Double currentRaceSpeed = calculateCurrentRaceSpeed(completedText, startTime);
        List<KeyStats> oldKeyStatsList = user.getAllKeys();
        Map<Character, KeyStats> keyStatsMap = new HashMap<>();

        oldKeyStatsList.forEach(keyStats -> keyStatsMap.put(keyStats.getCharacter(), keyStats));

        Map<Character, Integer> gameTextCounts = new HashMap<>();
        Map<Character, Integer> failedCharactersCounts = new HashMap<>();

        countGameLetters(gameTextCounts, completedText);
        countFailedChars(failedCharactersCounts);
        updateUserKeyStats(gameTextCounts, keyStatsMap, failedCharactersCounts, user);

        List<KeyStats> newKeyStatsList = new ArrayList<>();
        keyStatsMap.forEach((character, keyStats) -> newKeyStatsList.add(keyStats));

        setUserStatsSinglePlayer(user, currentRaceSpeed, newKeyStatsList);

        return user;
    }

    /**
     * Calculates stats for a player for a multiplayer game.
     *
     * @param user      - user associated with the player
     * @param gameText  - text provided by the game
     * @param startTime - the time the user started playing
     * @return User with updated stats and keyStats
     */
    public User calculateMultiplayerStats(User user, List<Character> gameText, Long startTime) {
        List<Character> completedText = gameText.subList(0, position);
        Double currentRaceSpeed = calculateCurrentRaceSpeed(completedText, startTime);
        List<KeyStats> oldKeyStatsList = user.getAllKeys();
        Map<Character, KeyStats> keyStatsMap = new HashMap<>();

        oldKeyStatsList.forEach(keyStats -> keyStatsMap.put(keyStats.getCharacter(), keyStats));

        Map<Character, Integer> gameTextCounts = new HashMap<>();
        Map<Character, Integer> failedCharactersCounts = new HashMap<>();
        countGameLetters(gameTextCounts, completedText);
        countFailedChars(failedCharactersCounts);
        updateUserKeyStats(gameTextCounts, keyStatsMap, failedCharactersCounts, user);
        List<KeyStats> newKeyStatsList = new ArrayList<>();
        keyStatsMap.forEach((character, keyStats) -> newKeyStatsList.add(keyStats));

        setUserStatsMultiplayer(user, currentRaceSpeed, newKeyStatsList);

        return user;
    }

    /**
     * Helper method that updates key stats values for each character after the game is completed or the player has left the game.
     *
     * @param gameTextCounts         - counts of each character the user has typed
     * @param keyStatsMap            - old key stats
     * @param failedCharactersCounts - failed character counts
     * @param user                   - user associated with the stats
     */
    private void updateUserKeyStats(Map<Character, Integer> gameTextCounts, Map<Character, KeyStats> keyStatsMap, Map<Character, Integer> failedCharactersCounts, User user) {
        gameTextCounts.forEach((character, count) -> {
            KeyStats keyStats = keyStatsMap.get(character);

            if (keyStats == null) {
                keyStats = new KeyStats(character);
            }
            keyStats.setNumSuccesses(count.longValue() + keyStats.getNumSuccesses());
            keyStats.setUser(user);
            keyStatsMap.put(character, keyStats);
        });

        failedCharactersCounts.forEach((character, count) -> {
            KeyStats keyStats = keyStatsMap.get(character);

            if (keyStats == null) {
                keyStats = new KeyStats(character);
            }
            keyStats.setNumFails(count.longValue() + keyStats.getNumFails());
            keyStats.setUser(user);
            keyStatsMap.put(character, keyStats);
        });
    }

    /**
     * Helper method to calculate player's last speed.
     *
     * @param gameText  - characters the user has typed
     * @param startTime - starting time
     * @return Double - speed from the last game
     */
    private Double calculateCurrentRaceSpeed(List<Character> gameText, Long startTime) {
        Double totalTime = (double) (endTime - startTime) / 60000;
        Integer textLength = gameText.size();
        Integer lastWord = textLength % 5 > 0 ? 1 : 0;
        Integer words = (position / 5) + lastWord;
        Double currentRaceSpeed = (double) Math.round((words / totalTime) * 100d) / 100d;
        return currentRaceSpeed;
    }

    /**
     * Helper method to calculate new average speed based on the latest result.
     *
     * @param userStats        - stats to be updated
     * @param currentRaceSpeed - latest speed
     * @return Double - new average speed
     */
    private Double calculateNewAverageSpeed(Stats userStats, Double currentRaceSpeed) {
        Double numerator = (userStats.getAverageSpeed() * (userStats.getNumMultiGamesCompleted() + userStats.getNumSingleGamesCompleted() - 1) + currentRaceSpeed);
        Integer denominator = userStats.getNumSingleGamesCompleted() + userStats.getNumMultiGamesCompleted();
        Double newAverageSpeed = (double) Math.round((numerator / denominator) * 100d) / 100d;
        return newAverageSpeed;
    }

    /**
     * Calculate total number of successes to get the overall accuracy.
     *
     * @param keyStatsList - updated key stats
     * @return Double - overall accuracy for the user
     */
    private Double calculateSuccessRate(List<KeyStats> keyStatsList) {
        double totalCount = 0;
        double successes = 0;
        for (KeyStats keyStats : keyStatsList) {
            totalCount += keyStats.getNumSuccesses() + keyStats.getNumFails();
            successes += keyStats.getNumSuccesses();
        }
        Double successRate = Math.round((successes / totalCount) * 10000d) / 100d;
        return successRate;
    }

    /**
     * Helper method to calculate the frequency of each letter in the game text.
     *
     * @param gameTextCounts - a map to store the results in
     * @param gameText       - text that player has typed.
     */
    private void countGameLetters(Map<Character, Integer> gameTextCounts, List<Character> gameText) {
        for (Character character : gameText) {
            if (gameTextCounts.containsKey(character)) {
                gameTextCounts.put(character, gameTextCounts.get(character) + 1);
            } else {
                gameTextCounts.put(character, 1);
            }
        }
    }

    /**
     * Helper method to calculate the frequency of failed characters.
     *
     * @param failedCharactersCounts - a map to store the frequency.
     */
    private void countFailedChars(Map<Character, Integer> failedCharactersCounts) {
        for (Character character : failedCharacters) {
            if (failedCharactersCounts.containsKey(character)) {
                failedCharactersCounts.put(character, failedCharactersCounts.get(character) + 1);
            } else {
                failedCharactersCounts.put(character, 1);
            }
        }
    }

    /**
     * Sets the updated user stats for a multiplayer game.
     *
     * @param user             - user associated with the results
     * @param currentRaceSpeed - last speed
     * @param newKeyStatsList  - updated key stats
     */
    private void setUserStatsMultiplayer(User user, Double currentRaceSpeed, List<KeyStats> newKeyStatsList) {
        Stats userStats = user.getUserStats();
        userStats.setLastRaceSpeed(currentRaceSpeed);
        if (winner) {
            userStats.incrementRacesWon();
        }
        if (currentRaceSpeed > userStats.getBestRaceSpeed()) {
            userStats.setBestRaceSpeed(currentRaceSpeed);
        }
        userStats.incrementNumMultiGamesCompleted();
        Double newAverageSpeed = calculateNewAverageSpeed(userStats, currentRaceSpeed);
        userStats.setAverageSpeed(newAverageSpeed);
        userStats.setAccuracy(calculateSuccessRate(newKeyStatsList));
        user.setUserStats(userStats);
        user.setAllKeys(newKeyStatsList);
    }

    /**
     * Sets the updated user stats for a single player game.
     *
     * @param user             - user associated with the results
     * @param currentRaceSpeed - last speed
     * @param newKeyStatsList  - updated key stats
     */
    private void setUserStatsSinglePlayer(User user, Double currentRaceSpeed, List<KeyStats> newKeyStatsList) {
        Stats userStats = user.getUserStats();
        userStats.setLastRaceSpeed(currentRaceSpeed);
        if (currentRaceSpeed > userStats.getBestRaceSpeed()) {
            userStats.setBestRaceSpeed(currentRaceSpeed);
        }
        userStats.incrementNumSingleGamesCompleted();
        Double newAverageSpeed = calculateNewAverageSpeed(userStats, currentRaceSpeed);
        userStats.setAverageSpeed(newAverageSpeed);
        userStats.setAccuracy(calculateSuccessRate(newKeyStatsList));
        user.setUserStats(userStats);
        user.setAllKeys(newKeyStatsList);
    }

    @Override
    public String toString() {
        return "Player{" +
                "username='" + username + '\'' +
                ", position=" + position +
                ", failedCharacters=" + failedCharacters +
                ", incorrectCharacters=" + incorrectCharacters +
                ", gameId='" + gameId + '\'' +
                ", winner=" + winner +
                ", endTime=" + endTime +
                ", playerNumber=" + playerNumber +
                '}';
    }
}
