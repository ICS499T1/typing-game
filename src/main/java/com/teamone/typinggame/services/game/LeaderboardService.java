package com.teamone.typinggame.services.game;

import com.teamone.typinggame.models.User;

import java.util.List;


public interface LeaderboardService {
    public List<User> fetchTopPlayers();
}
