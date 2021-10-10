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
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "userID", nullable = false)
    private Long userID;

    @Column(nullable = false, unique = true)
    private String username;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, optional = false)
    @PrimaryKeyJoinColumn
    private Stats userStats;

    @OneToMany(mappedBy = "user")
    private List<KeyStats> allKeys;

    public void setUsername(String username) {
        this.username = username;
    }

    public void setUserStats(Stats userStats) {
        this.userStats = userStats;
    }

    public Long getUserID() {
        return userID;
    }

    public Stats getUserStats() {
        return userStats;
    }

    public String getUsername() {
        return username;
    }

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
