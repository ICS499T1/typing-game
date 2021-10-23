package com.teamone.typinggame.models;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import javax.persistence.*;

@Component
@Entity
@NoArgsConstructor
public class Player {
    @Id
    @Column(name="userID")
    @Getter
    private Long userID;

    @OneToOne(optional = false)
    @MapsId
    @JoinColumn(name = "userID", nullable = false, unique = true)
    @Getter
    private User user;

    private Integer position;
//    @Transient
//    private char[] completedText;
//    @Transient
//    private char[] incompleteText;

    public Player(User user) {
        this.user = user;
    }
}
