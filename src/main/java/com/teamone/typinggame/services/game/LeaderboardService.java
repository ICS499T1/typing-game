package com.teamone.typinggame.services.game;

import com.teamone.typinggame.models.Leaderboard;
import com.teamone.typinggame.models.User;
import org.springframework.data.jpa.repository.Query;

import java.util.Collection;
import java.util.List;

public interface LeaderboardService {
    /**
     * Grabs top twenty players from the database.
     *
     * @return Leaderboard - with top players
     */
    Leaderboard fetchTopStats();
}
