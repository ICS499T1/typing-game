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
    @OneToOne
    @PrimaryKeyJoinColumn
    private Stats userStats;
    @OneToMany(mappedBy = "user")
    private List<KeyStats> allKeys = new ArrayList<>(120);
}
