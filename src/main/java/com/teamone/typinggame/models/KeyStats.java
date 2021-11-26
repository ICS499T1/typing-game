package com.teamone.typinggame.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.springframework.stereotype.Component;

import javax.persistence.*;
import java.util.Map;

@Component
@Entity
@Table(
        name = "key_stats",
        uniqueConstraints =
            @UniqueConstraint(columnNames = {"userID", "character"}))
@JsonIgnoreProperties({"user", "keyID"})
public class KeyStats {
    @Column(nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long keyID;
    private Character character;
    private Long numFails;
    private Long numSuccesses;

    @ManyToOne
    @JoinColumn(name="userID", nullable = false)
    private User user;

    public Long getKeyID() {
        return keyID;
    }

    public KeyStats() {
    }

    public KeyStats(Character character) {
        this.character = character;
        this.numFails = 0L;
        this.numSuccesses = 0L;
    }

    public KeyStats(char character, long numFails, long numSuccesses, User user) {
        this.character = character;
        this.numFails = numFails;
        this.numSuccesses = numSuccesses;
        this.user = user;
    }

    public void addNumSuccesses(Long numSuccesses) { this.numSuccesses += numSuccesses; }

    public void addNumFails(Long numFails) { this.numFails += numFails; }

    public Character getCharacter() {
        return character;
    }

    public void setCharacter(char character) {
        this.character = character;
    }

    public long getNumFails() {
        return numFails;
    }

    public void setNumFails(long numFails) {
        this.numFails = numFails;
    }

    public Long getNumSuccesses() {
        return numSuccesses;
    }

    public void setNumSuccesses(long numSuccesses) {
        this.numSuccesses = numSuccesses;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Override
    public String toString() {
        return "KeyStats{" +
                "character=" + character +
                ", numFails=" + numFails +
                ", numSuccesses=" + numSuccesses +
                '}';
    }
}
