package com.teamone.typinggame.models.game;

import com.teamone.typinggame.models.GameStatus;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public abstract class GameInterface {

    protected final ReadWriteLock gameRwLock = new ReentrantReadWriteLock();
    protected final ReadWriteLock playerSetLock = new ReentrantReadWriteLock();
    protected final ReadWriteLock doneLock = new ReentrantReadWriteLock();

    @Getter
    @Setter
    private String gameId;

    @Getter
    @Setter
    private List<Character> gameText;

    private GameStatus status;

    @Getter
    @Setter
    private Long startTime;

    private Integer doneCount;

    public GameStatus getStatus() {
        gameRwLock.readLock().lock();
        try {
            return status;
        } finally {
            gameRwLock.readLock().unlock();
        }
    }

    public void setStatus(GameStatus status) {
        gameRwLock.writeLock().lock();
        try {
            this.status = status;
        } finally {
            gameRwLock.writeLock().unlock();
        }
    }

    public Integer getDoneCount() {
        doneLock.readLock().lock();
        try {
            return doneCount;
        } finally {
            doneLock.readLock().unlock();
        }
    }

    public void setDoneCount(Integer newCount) {
        doneLock.readLock().lock();
        try {
            doneCount = newCount;
        } finally {
            doneLock.readLock().unlock();
        }
    }

    /**
     * Increments the number of players that are done.
     */
    public void incrementDoneCount() {
        doneLock.writeLock().lock();
        try {
            doneCount++;
        } finally {
            doneLock.writeLock().unlock();
        }
    }

    /**
     * Decrements the number of players that are done.
     */
    public void decrementDoneCount() {
        doneLock.writeLock().lock();
        try {
            doneCount--;
        } finally {
            doneLock.writeLock().unlock();
        }
    }

    /**
     * Sets text for the game by making a request to the API.
     */
    public void fillGameText() {
        gameText = new ArrayList<>();
        String url = "http://metaphorpsum.com/sentences/5";
        RestTemplate restTemplate = new RestTemplate();
        String paragraph = restTemplate.getForObject(url, String.class);


        char[] characterArray = paragraph.toCharArray();
        for (char c : characterArray) {
            gameText.add(c);
        }
    }

    /**
     * Sets the initial game text (before it is started) to empty
     */
    public void initialGameText() {
        gameText = new ArrayList<>();
        gameText.add(' ');
    }

}
