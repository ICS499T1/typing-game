package com.teamone.typinggame.services;

import com.teamone.typinggame.models.Stats;

public interface StatsService {
    Stats updateStatsMultiplayer(Long statsId, Double speed, Boolean victory);
    Stats updateStatsSinglePlayer(Long statsId, Double speed);
}
