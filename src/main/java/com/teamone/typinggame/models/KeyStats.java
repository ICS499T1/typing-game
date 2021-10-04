package com.teamone.typinggame.models;

import org.springframework.stereotype.Component;

import javax.persistence.*;
import java.util.Map;

@Component
@Entity
@Table(name = "key_stats")
public class KeyStats {
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Id
    private Long keyID;
    private char character;
    private long numFails;
    private long numSuccesses;
    @ManyToOne
    @JoinColumn(name="userID", nullable = false)
    private User user;
}
