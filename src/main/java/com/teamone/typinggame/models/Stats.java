package com.teamone.typinggame.models;

import org.springframework.stereotype.Component;

import javax.persistence.*;

@Component
@Entity
@Table(name = "stats")
public class Stats {
    @OneToOne(mappedBy = "users", cascade = CascadeType.ALL)
    @PrimaryKeyJoinColumn
    private User user;
    private double averageSpeed;
    private int numRacesCompleted;
    private int racesWon;
    private int lastRaceSpeed;
    private int bestRaceSpeed;
}
