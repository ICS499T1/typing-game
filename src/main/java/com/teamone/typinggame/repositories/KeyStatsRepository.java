package com.teamone.typinggame.repositories;

import com.teamone.typinggame.models.KeyStats;
import com.teamone.typinggame.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;


public interface KeyStatsRepository extends JpaRepository<KeyStats, Long> {
    public List<KeyStats> findByUser(User user);
}