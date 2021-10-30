package com.teamone.typinggame.services.user;

import com.teamone.typinggame.models.Stats;

public interface StatsService {
    Stats updateStatsMultiplayer(Long statsId, Double speed, Boolean victory);
    Stats updateStatsSinglePlayer(Long statsId, Double speed);
}
