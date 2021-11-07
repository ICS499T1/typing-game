package com.teamone.typinggame.services.game;

import com.teamone.typinggame.models.User;
import org.springframework.data.jpa.repository.Query;

import java.util.Collection;

public interface LeaderboardService {
    Collection<User> fetchTopUsers();
}
