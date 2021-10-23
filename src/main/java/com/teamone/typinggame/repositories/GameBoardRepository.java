package com.teamone.typinggame.repositories;

import com.teamone.typinggame.models.GameBoard;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GameBoardRepository extends JpaRepository<GameBoard, Long> {
}