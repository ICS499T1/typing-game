package com.teamone.typinggame.services.user;

import com.teamone.typinggame.models.Stats;
import com.teamone.typinggame.repositories.StatsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class StatsServiceImpl implements StatsService {
    private final StatsRepository statsRepository;

    @Autowired
    public StatsServiceImpl(StatsRepository statsRepository) {
        this.statsRepository = statsRepository;
    }

    public Stats updateStatsMultiplayer(Long statsId, Double speed, Boolean victory) {
        Stats stats = statsRepository.getById(statsId);
        updateMultiplayerAvgSpeed(stats, speed, victory);
        updateBestRace(stats, speed);
        updateLastRace(stats, speed);
        updateRacesWon(stats, victory);
        return statsRepository.saveAndFlush(stats);
    }

    public Stats updateStatsSinglePlayer(Long statsId, Double speed) {
        Stats stats = statsRepository.getById(statsId);
        updateSingleAvgSpeed(stats, speed);
        updateBestRace(stats, speed);
        updateLastRace(stats, speed);
        return statsRepository.saveAndFlush(stats);
    }

    private void updateSingleAvgSpeed(Stats stats, Double speed) {
        double currentAverage = stats.getAverageSpeed();
        int currentRaces = stats.getNumSingleGamesCompleted() + stats.getNumMultiGamesCompleted();
        double numerator = currentAverage * currentRaces + speed;
        int denominator = updateSingleNumOfRaces(stats) + stats.getNumMultiGamesCompleted();
        stats.setAverageSpeed(numerator/denominator);
    }

    private void updateMultiplayerAvgSpeed(Stats stats, Double speed, Boolean victory) {
        double currentAverage = stats.getAverageSpeed();
        int currentRaces = stats.getNumMultiGamesCompleted();
        double numerator = currentAverage * currentRaces + speed;
        double denominator = updateMultiNumOfRaces(stats) + stats.getNumSingleGamesCompleted();
        stats.setAverageSpeed(numerator/denominator);
    }

    private void updateBestRace(Stats stats, Double speed) {
        double currentBestSpeed = stats.getBestRaceSpeed();
        if (currentBestSpeed < speed) {
            stats.setBestRaceSpeed(speed);
        }
    }

    private void updateRacesWon(Stats stats, Boolean victory) {
        if (victory) stats.updateRacesWon();
    }

    private void updateLastRace(Stats stats, Double speed) {
        stats.setLastRaceSpeed(speed);
    }

    private int updateSingleNumOfRaces(Stats stats) {
        stats.updateNumSingleGamesCompleted();
        return stats.getNumSingleGamesCompleted();
    }

    private int updateMultiNumOfRaces(Stats stats) {
        stats.updateNumMultiGamesCompleted();
        return stats.getNumMultiGamesCompleted();
    }
}
