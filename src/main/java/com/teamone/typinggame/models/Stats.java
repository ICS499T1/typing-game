package com.teamone.typinggame.models;

import org.springframework.stereotype.Component;

import javax.persistence.*;

@Component
@Entity
@Table(name = "stats")
public class Stats {
    @Id
    @Column(name="userID")
    private Long userID;

    @OneToOne
    @MapsId
    @JoinColumn(name = "userID")
    private User user;
    private double averageSpeed;
    private int numRacesCompleted;
    private int racesWon;
    private int lastRaceSpeed;
    private int bestRaceSpeed;

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
