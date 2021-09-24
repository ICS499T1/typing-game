package com.gameplay.logic;

public class GameText {
    private String gameText;
    private Word[] words;

    public GameText(String gameText) {
        this.gameText = gameText;
        divideintoWords();
    }

    private void divideintoWords() {
        // this method will divide gameText into 5 character words and store them in the words array
        return;
    }

    public Word[] getWords() {
        return words;
    }
}
