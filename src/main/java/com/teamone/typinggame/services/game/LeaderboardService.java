package com.teamone.typinggame.services.game;

import com.teamone.typinggame.models.User;

import java.util.Collection;

public interface LeaderboardService {
    public Collection<User> fetchTopPlayers();
}
