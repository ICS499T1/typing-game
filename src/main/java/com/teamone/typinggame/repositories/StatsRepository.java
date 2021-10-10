package com.teamone.typinggame.repositories;

import com.teamone.typinggame.models.Stats;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StatsRepository extends JpaRepository<Stats, Long> {
}