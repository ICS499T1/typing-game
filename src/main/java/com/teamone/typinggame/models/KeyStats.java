package com.teamone.typinggame.models;

import org.springframework.stereotype.Component;

import javax.persistence.*;
import java.util.Map;

@Component
@Entity
@Table(name = "key_stats")
public class KeyStats {
    @Column(nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long keyID;
    private char character;
    private long numFails;
    private long numSuccesses;
    @ManyToOne
    @JoinColumn(name="userID", nullable = false)
    private User user;

    public Long getKeyID() {
        return keyID;
    }

    public KeyStats() {
    }

    public KeyStats(char character, long numFails, long numSuccesses, User user) {
        this.character = character;
        this.numFails = numFails;
        this.numSuccesses = numSuccesses;
        this.user = user;
    }
}
