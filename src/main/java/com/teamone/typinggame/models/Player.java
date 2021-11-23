package com.teamone.typinggame.models;

import com.teamone.typinggame.services.user.UserService;
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

    public void incrementPosition() {
        position++;
    }

    public void addFailedCharacter(Character character) {
        failedCharacters.add(character);
    }

    public void addIncorrectCharacter(Character character) {
        incorrectCharacters.add(character);
    }

    public void removeIncorrectCharacter() {
        incorrectCharacters.pop();
    }

    public void reset() {
        position = 0;
        failedCharacters = new ArrayList<>();
        incorrectCharacters = new Stack<>();
        winner = false;
        endTime = 0L;
    }

    public User calculateStats(User user, List<Character> gameText, Long startTime) {
        List<Character> completedText = gameText.subList(0, position);
        Double currentRaceSpeed = calculateCurrentRaceSpeed(completedText, startTime);
        List<KeyStats> oldKeyStatsList = user.getAllKeys();
        Map<Character, KeyStats> keyStatsMap = new HashMap<>();

        oldKeyStatsList.forEach(keyStats -> keyStatsMap.put(keyStats.getCharacter(), keyStats));

        Map<Character, Integer> gameTextCounts = new HashMap<>();
        Map<Character, Integer> failedCharactersCounts = new HashMap<>();
        countGameLetters(gameTextCounts, completedText);
        countFailedChars(failedCharactersCounts);
        gameTextCounts.forEach((c, count) -> System.out.println("Char: " + c + " Count: " + count));
        // Fixed key stats loading logic
        gameTextCounts.forEach((character, count) -> {
            KeyStats keyStats = new KeyStats(character);
            Integer numFailsThisMatch = failedCharactersCounts.get(character);
            if (numFailsThisMatch != null) {
                keyStats.setNumFails(numFailsThisMatch.longValue());
                keyStats.setNumSuccesses(count.longValue() - numFailsThisMatch);
            } else {
                keyStats.setNumSuccesses(count.longValue());
                keyStats.setNumFails(0L);
            }
            keyStats.setUser(user);
            keyStatsMap.put(character, keyStats);
        });

        List<KeyStats> newKeyStatsList = new ArrayList<>();
        keyStatsMap.forEach((character, keyStats) -> newKeyStatsList.add(keyStats));

        setUserStatsMultiplayer(user, currentRaceSpeed, newKeyStatsList);

        return user;
    }

    private Double calculateCurrentRaceSpeed(List<Character> gameText, Long startTime) {
        Double totalTime = (double) (endTime - startTime) / 60000;
        Integer textLength = gameText.size();
        Integer lastWord = textLength % 5 > 0 ? 1 : 0;
        Integer words = (position / 5) + lastWord;
        Double currentRaceSpeed = (double) Math.round((words / totalTime) * 100d) / 100d;
        return currentRaceSpeed;
    }

    private void countGameLetters(Map<Character, Integer> gameTextCounts, List<Character> gameText) {
        for (Character character : gameText) {
            if (gameTextCounts.containsKey(character)) {
                gameTextCounts.put(character, gameTextCounts.get(character) + 1);
            } else {
                gameTextCounts.put(character, 1);
            }
        }
    }

    private void countFailedChars(Map<Character, Integer> failedCharactersCounts) {
        if (failedCharacters.isEmpty()) return;
        for (Character character : failedCharacters) {
            if (failedCharactersCounts.containsKey(character)) {
                failedCharactersCounts.put(character, failedCharactersCounts.get(character) + 1);
            } else {
                failedCharactersCounts.put(character, 1);
            }
        }
        System.out.println("Failed char counts size: " + failedCharactersCounts.size());
    }

    private void setUserStatsMultiplayer(User user, Double currentRaceSpeed, List<KeyStats> newKeyStatsList) {
        Stats userStats = user.getUserStats();
        userStats.setLastRaceSpeed(currentRaceSpeed);
        if (winner) {
            userStats.incrementRacesWon();
        }
        if (currentRaceSpeed > userStats.getBestRaceSpeed()) {
            userStats.setBestRaceSpeed(currentRaceSpeed);
        }
        // TODO add logic for incrementing single games
        userStats.incrementNumMultiGamesCompleted();
        Double numerator = (userStats.getAverageSpeed() * (userStats.getNumMultiGamesCompleted() + userStats.getNumSingleGamesCompleted() - 1) + currentRaceSpeed);
        Integer denominator = userStats.getNumSingleGamesCompleted() + userStats.getNumMultiGamesCompleted();
        Double newAverageSpeed = (double) Math.round((numerator/denominator) * 100d) / 100d;
        userStats.setAverageSpeed(newAverageSpeed);
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
