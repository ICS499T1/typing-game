package com.teamone.typinggame.services;

import com.teamone.typinggame.models.Stats;
import com.teamone.typinggame.repositories.StatsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class StatsService {
    private final StatsRepository statsRepository;

    @Autowired
    public StatsService(StatsRepository statsRepository) {
        this.statsRepository = statsRepository;
    }

    public void updateNumOfRaces(Stats stats) {
        stats.updateNumRacesCompleted();
    }

    public void updateAvgSpeed(Stats stats) {

    }
}
