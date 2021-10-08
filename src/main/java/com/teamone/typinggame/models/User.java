package com.teamone.typinggame.models;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.stereotype.Component;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Component
@Entity
@Table(name = "users")
public class User {
    private String username;
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Id
    @Column(name="userID")
    private Long userID;
    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    @PrimaryKeyJoinColumn
    private Stats userStats;
    @OneToMany(mappedBy = "user")
    private List<KeyStats> allKeys = new ArrayList<>(120);

    public User() {
    }

    public User(String username, Stats userStats, List<KeyStats> allKeys) {
        this.username = username;
        this.userStats = userStats;
        this.allKeys = allKeys;
    }

    @Override
    public String toString() {
        return "User{" +
                "username='" + username + '\'' +
                ", userStats=" + userStats +
                ", allKeys=" + allKeys +
                '}';
    }
}
