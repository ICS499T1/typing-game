package com.teamone.typinggame.models;

import org.springframework.stereotype.Component;

import javax.persistence.*;
import java.util.Map;

@Component
@Entity
@Table(name = "key_stats")
public class KeyStats {
    private char character;
    private long numFails;
    private long numSuccesses;
    @OneToOne
    private User user;
}
