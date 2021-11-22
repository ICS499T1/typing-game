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

    public void calculateStats(Long startTime, List<Character> gameText, User user) {
        Long totalTime = endTime - startTime;
        Integer textLength = gameText.size();
        Integer lastWord = textLength % 5 > 0 ? 1 : 0;
        Integer words = (position/5) + lastWord;
        Double currentRaceSpeed = ((double) words / (totalTime/60000));

        List<KeyStats> oldKeyStatsList = user.getAllKeys();
        Map<Character, KeyStats> keyStatsMap = new HashMap<>();

        for (KeyStats keyStats : oldKeyStatsList) {
            keyStatsMap.put(keyStats.getCharacter(), keyStats);
        }

        Map<Character, Integer> gameTextCounts = new HashMap<>();
        Map<Character, Integer> failedCharactersCounts = new HashMap<>();
        for (Character character : gameText) {
            if (gameTextCounts.containsKey(character)) {
                gameTextCounts.put(character, gameTextCounts.get(character) + 1);
            } else {
                gameTextCounts.put(character, 1);
            }
        }

        for (Character character : failedCharacters) {
            if (failedCharactersCounts.containsKey(character)) {
                failedCharactersCounts.put(character, failedCharactersCounts.get(character) + 1);
            } else {
                failedCharactersCounts.put(character, 1);
            }
        }

        gameTextCounts.forEach((character, count) -> {
            if (keyStatsMap.containsKey(character)) {
                KeyStats keyStats = keyStatsMap.get(character);
                keyStats.addNumSuccesses(Integer.toUnsignedLong(count));
                Integer numFailsThisMatch = failedCharactersCounts.get(character);
                keyStats.addNumFails(Integer.toUnsignedLong(numFailsThisMatch));
            } else {
                KeyStats keyStats = new KeyStats(character);
                keyStats.setNumSuccesses(Integer.toUnsignedLong(count));
                keyStats.setNumFails(Integer.toUnsignedLong(count));
                keyStatsMap.put(character, keyStats);
            }
        });

        List<KeyStats> newKeyStatsList = new ArrayList<>();

        keyStatsMap.forEach(((character, keyStats) -> {
            newKeyStatsList.add(keyStats);
        }));

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

        userStats.setAverageSpeed(numerator/denominator);
        user.setAllKeys(newKeyStatsList);
    }
}
