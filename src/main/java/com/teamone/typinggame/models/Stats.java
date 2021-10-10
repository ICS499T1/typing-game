package com.teamone.typinggame.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.springframework.stereotype.Component;

import javax.persistence.*;

@Component
@Entity
@Table(name = "stats")
@JsonIgnoreProperties(value={"user"})
public class Stats {
    @Id
    @Column(name="userID")
    private Long userID;

    @OneToOne(optional = false)
    @MapsId
    @JoinColumn(name = "userID", nullable = false, unique = true)
    private User user;
    private double averageSpeed;
    private int numRacesCompleted;
    private int racesWon;
    private int lastRaceSpeed;
    private int bestRaceSpeed;

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

    public int getNumRacesCompleted() {
        return numRacesCompleted;
    }

    public void setNumRacesCompleted(int numRacesCompleted) {
        this.numRacesCompleted = numRacesCompleted;
    }

    public void updateNumRacesCompleted() {
        this.numRacesCompleted++;
    }

    public int getRacesWon() {
        return racesWon;
    }

    public void setRacesWon(int racesWon) {
        this.racesWon = racesWon;
    }

    public int getLastRaceSpeed() {
        return lastRaceSpeed;
    }

    public void setLastRaceSpeed(int lastRaceSpeed) {
        this.lastRaceSpeed = lastRaceSpeed;
    }

    public int getBestRaceSpeed() {
        return bestRaceSpeed;
    }

    public void setBestRaceSpeed(int bestRaceSpeed) {
        this.bestRaceSpeed = bestRaceSpeed;
    }

    public Stats() {
    }

    public Stats(User user, double averageSpeed, int numRacesCompleted, int racesWon, int lastRaceSpeed, int bestRaceSpeed) {
        this.user = user;
        this.averageSpeed = averageSpeed;
        this.numRacesCompleted = numRacesCompleted;
        this.racesWon = racesWon;
        this.lastRaceSpeed = lastRaceSpeed;
        this.bestRaceSpeed = bestRaceSpeed;
    }
}
