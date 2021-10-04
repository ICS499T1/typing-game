package com.teamone.typinggame.models;

import org.springframework.stereotype.Component;

import javax.persistence.*;

@Component
@Entity
@Table(name = "stats")
public class Stats {
    @Id
    @Column(name="statsID")
    private Long statsID;

//    @OneToOne (mappedBy = "userID", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
//    @PrimaryKeyJoinColumn(name="userID", referencedColumnName="userID")
//    private User user;
    private double averageSpeed;
    private int numRacesCompleted;
    private int racesWon;
    private int lastRaceSpeed;
    private int bestRaceSpeed;
}
