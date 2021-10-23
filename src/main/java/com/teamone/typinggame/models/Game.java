package com.teamone.typinggame.models;

import lombok.*;
import org.springframework.stereotype.Component;

import javax.persistence.*;
import java.util.List;
import java.util.Set;

@NoArgsConstructor
@Component
@Entity
public class Game {
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Id
    @Column(name = "id", nullable = false)
    @Getter
    private Long id;

    @Getter
    @Setter
    private String gameId;

    @OneToMany
    @JoinColumn(name = "game_id", unique = true)
    @Getter
    @Setter
    private Set<User> users;

    @Getter
    @Setter
    private GameStatus status;

    @OneToOne(cascade = CascadeType.ALL, optional = false, orphanRemoval = true)
    @JoinColumn(name = "game_board_id", nullable = false, unique = true)
    @Getter
    @Setter
    private GameBoard gameBoard;

    @Getter
    @Setter
    private String winner;
}
