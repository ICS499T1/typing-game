package com.gameplay.logic;
import com.user.logic.User;

public class GameSpawner {
    private User user;
    private String gameText;

    // Use singleton pattern
    //create an object of SingleObject
    private static GameSpawner singleSpawner = new GameSpawner();

    //make the constructor private so that this class cannot be
    //instantiated
    private GameSpawner(){}

    //Get the only object available
    public static GameSpawner getInstance(){
        return singleSpawner;
    }

    // using requestType is not the best way to do it, rethink this
    public PlayArea generateNewPlayArea(User user, int requestType) {
        if (user.isInAnActiveSession()) {
            return null;
        }

        String gameText;
        if (requestType == 1) {
            gameText = getRandomParagraph();
        } else if (requestType == 2) {
            gameText = getRandomNumbers();
        } else if (requestType == 3) {
            gameText = getRandomText();
        } else {
            gameText = "invalid";
        }

        return new PlayArea(user, gameText);
    }

    public String getRandomParagraph() {
        // get random paragraph from API and return it
        return "random paragraph";
    }

    public String getRandomNumbers() {
        return "1234";
    }

    public String getRandomText() {
        return "random text";
    }

}
