package com.teamone.typinggame.services;

import com.teamone.typinggame.models.Stats;
import com.teamone.typinggame.models.User;
import com.teamone.typinggame.repositories.StatsRepository;
import com.teamone.typinggame.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

@Service
public class UserService {
    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User newUser(User user) {
        Stats userStats = new Stats(user, 0.0, 0, 0, 0, 0);
        user.setUserStats(userStats);
        userStats.setUser(user);
        return userRepository.saveAndFlush(user);
    }
}
