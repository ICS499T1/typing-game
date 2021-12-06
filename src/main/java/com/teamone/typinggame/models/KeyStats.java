package com.teamone.typinggame.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.springframework.stereotype.Component;

import javax.persistence.*;

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
    @JoinColumn(name = "userID", nullable = false)
    private User user;

    public Long getKeyID() {
        return keyID;
    }

    /**
     * Basic constructor for key stats object.
     */
    public KeyStats() {
    }

    /**
     * Basic constructor for key stats object that requires a character input.
     *
     * @param character - input character
     */
    public KeyStats(Character character) {
        this.character = character;
        this.numFails = 0L;
        this.numSuccesses = 0L;
    }

    /**
     * Basic constructor for key stats object that requires several parameters.
     *
     * @param character    - input character
     * @param numFails     - number of fails for a specific character
     * @param numSuccesses - number of successes for a specific character
     * @param user         - user who the key stats are associated with
     */
    public KeyStats(char character, long numFails, long numSuccesses, User user) {
        this.character = character;
        this.numFails = numFails;
        this.numSuccesses = numSuccesses;
        this.user = user;
    }

    /**
     * Adds the number of successes to an already existing value.
     *
     * @param numSuccesses - additional successes
     */
    public void addNumSuccesses(Long numSuccesses) {
        this.numSuccesses += numSuccesses;
    }

    /**
     * Adds the number of fails to an already existing value.
     *
     * @param numFails - additional fails
     */
    public void addNumFails(Long numFails) {
        this.numFails += numFails;
    }

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
