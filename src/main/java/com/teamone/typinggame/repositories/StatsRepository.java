package com.teamone.typinggame.repositories;

import com.teamone.typinggame.models.Stats;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface StatsRepository extends JpaRepository<Stats, Long> {
    @Query(value = "SELECT * FROM stats WHERE userid = ?1", nativeQuery = true)
    Stats getByUserId(long userId);
}