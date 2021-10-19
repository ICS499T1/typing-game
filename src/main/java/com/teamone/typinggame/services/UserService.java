package com.teamone.typinggame.services;

import com.teamone.typinggame.models.KeyStats;
import com.teamone.typinggame.models.Stats;
import com.teamone.typinggame.models.User;
import com.teamone.typinggame.repositories.StatsRepository;
import com.teamone.typinggame.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserService {
    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User newUser(User user) {
        Stats userStats = new Stats(user, 0.0, 0, 0, 0, 0, 0);
        user.setUserStats(userStats);
        userStats.setUser(user);
        KeyStats keyStats = new KeyStats('a', 5, 10, user);
        KeyStats keyStats2 = new KeyStats('b', 10, 10, user);
        List<KeyStats> keyStatsList= new ArrayList<>();
        keyStatsList.add(keyStats);
        keyStatsList.add(keyStats2);
        user.setAllKeys(keyStatsList);
        return userRepository.saveAndFlush(user);
    }

    public List<User> getUsers() {
        return userRepository.findAll();
    }
}
