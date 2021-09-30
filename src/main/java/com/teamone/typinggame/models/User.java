package com.teamone.typinggame.models;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.stereotype.Component;

import javax.persistence.*;
import java.util.ArrayList;

@Component
@Entity
@Table(name = "users")
public class User {
    private String username;
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Id
    private Long userID;
    @OneToOne(mappedBy = "stats", cascade = CascadeType.ALL)
    @PrimaryKeyJoinColumn
    private Stats userStats;
    @OneToMany(mappedBy = "users")
    private ArrayList<KeyStats> allKeys = new ArrayList<>(120);
}
