package com.teamone.typinggame.services.game;

import com.teamone.typinggame.models.User;
import com.teamone.typinggame.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;

@Service
public class LeaderboardServiceImpl implements LeaderboardService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public Collection<User> fetchTopUsers() {
        return userRepository.fetchTopTwentyUsers();
    }
}
