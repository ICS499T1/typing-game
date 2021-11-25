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

    public void incrementDoneCount() {
        doneLock.writeLock().lock();
        try {
            doneCount++;
        } finally {
            doneLock.writeLock().unlock();
        }
    }

    public void decrementDoneCount() {
        doneLock.writeLock().lock();
        try {
            doneCount--;
        } finally {
            doneLock.writeLock().unlock();
        }
    }

    public void fillGameText() {
        gameText = new ArrayList<>();
        //TODO change back to 10 sentences at end of url
        String url = "http://metaphorpsum.com/paragraphs/1/5";
        RestTemplate restTemplate = new RestTemplate();
        String paragraph = restTemplate.getForObject(url, String.class);


        char[] characterArray = paragraph.toCharArray();
        for (char c : characterArray) {
            gameText.add(c);
        }
    }

    public void initialText() {
        gameText = new ArrayList<>();
        String initialText = "Welcome to Space Racer! Invite your friends using the link above!!";
        char[] characterArray = initialText.toCharArray();
        for (char c : characterArray) {
            gameText.add(c);
        }
    }

}
