package com.teamone.typinggame.repositories;

import com.teamone.typinggame.models.Game;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GameRepository extends JpaRepository<Game, Long> {
    Game findByGameId(String gameId);
    boolean existsByGameId(String gameId);
}