package com.teamone.typinggame.models;

import lombok.*;
import org.springframework.stereotype.Component;

import javax.persistence.*;
import java.util.List;
import java.util.Set;

@NoArgsConstructor
@Component
@Entity
@RequiredArgsConstructor
public class GameBoard {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    @Getter
    private Long id;

    @Getter
    @Setter
    private String gameText;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "game_board_id", unique = true)
    @Getter
    @Setter
    private Set<Player> players;
}
