package com.teamone.typinggame.services.user;

import com.teamone.typinggame.models.KeyStats;
import com.teamone.typinggame.models.User;

import java.util.List;

public interface KeyStatsService {
    List<KeyStats> getUserKeys(User user);
}
