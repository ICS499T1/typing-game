package com.teamone.typinggame.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.springframework.stereotype.Component;

import javax.persistence.*;

@Component
@Entity
@Table(name = "stats")
@JsonIgnoreProperties(value = {"user"})
public class Stats {
    @Id
    @Column(name = "userID")
    private Long userID;

    @OneToOne(optional = false)
    @MapsId
    @JoinColumn(name = "userID", nullable = false, unique = true)
    private User user;
    private double averageSpeed;
    private int numSingleGamesCompleted;
    private int numMultiGamesCompleted;
    private int racesWon;
    private double lastRaceSpeed;
    private double bestRaceSpeed;
    private double accuracy;

    /**
     * Increments victories if the user won.
     */
    public void incrementRacesWon() {
        racesWon++;
    }

    /**
     * Increments number of single player games completed.
     */
    public void incrementNumSingleGamesCompleted() {
        numSingleGamesCompleted++;
    }

    /**
     * Increments number of multi player games completed.
     */
    public void incrementNumMultiGamesCompleted() {
        numMultiGamesCompleted++;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public double getAverageSpeed() {
        return averageSpeed;
    }

    public void setAverageSpeed(double averageSpeed) {
        this.averageSpeed = averageSpeed;
    }

    public int getNumSingleGamesCompleted() {
        return numSingleGamesCompleted;
    }

    public void setNumSingleGamesCompleted(int numSingleGamesCompleted) {
        this.numSingleGamesCompleted = numSingleGamesCompleted;
    }

    public int getNumMultiGamesCompleted() {
        return numMultiGamesCompleted;
    }

    public void setNumMultiGamesCompleted(int numMultiGamesCompleted) {
        this.numMultiGamesCompleted = numMultiGamesCompleted;
    }

    public int getRacesWon() {
        return racesWon;
    }

    public void setRacesWon(int racesWon) {
        this.racesWon = racesWon;
    }

    public double getLastRaceSpeed() {
        return lastRaceSpeed;
    }

    public void setLastRaceSpeed(double lastRaceSpeed) {
        this.lastRaceSpeed = lastRaceSpeed;
    }

    public double getBestRaceSpeed() {
        return bestRaceSpeed;
    }

    public void setBestRaceSpeed(double bestRaceSpeed) {
        this.bestRaceSpeed = bestRaceSpeed;
    }

    public double getAccuracy() {
        return accuracy;
    }

    public void setAccuracy(double accuracy) {
        this.accuracy = accuracy;
    }

    /**
     * Basic constructor to initialize stats.
     */
    public Stats() {
    }

    /**
     * Basic constructor to initialize stats.
     *
     * @param user                    - user associated with stats
     * @param averageSpeed            - average speed
     * @param numSingleGamesCompleted - number of single player games completed
     * @param numMultiGamesCompleted  - number of multiplayer games completed
     * @param racesWon                - number of races won
     * @param lastRaceSpeed           - last race speed
     * @param bestRaceSpeed           - bes race speed
     */
    public Stats(User user, double averageSpeed, int numSingleGamesCompleted, int numMultiGamesCompleted, int racesWon, int lastRaceSpeed, int bestRaceSpeed) {
        this.user = user;
        this.averageSpeed = averageSpeed;
        this.numSingleGamesCompleted = numSingleGamesCompleted;
        this.numMultiGamesCompleted = numMultiGamesCompleted;
        this.racesWon = racesWon;
        this.lastRaceSpeed = lastRaceSpeed;
        this.bestRaceSpeed = bestRaceSpeed;
    }

    @Override
    public String toString() {
        return "Stats{" +
                "userID=" + userID +
                ", averageSpeed=" + averageSpeed +
                ", numSingleGamesCompleted=" + numSingleGamesCompleted +
                ", numMultiGamesCompleted=" + numMultiGamesCompleted +
                ", racesWon=" + racesWon +
                ", lastRaceSpeed=" + lastRaceSpeed +
                ", bestRaceSpeed=" + bestRaceSpeed +
                '}';
    }
}
