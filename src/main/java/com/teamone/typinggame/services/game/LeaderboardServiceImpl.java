package com.teamone.typinggame.services.game;

import com.teamone.typinggame.models.User;
import com.teamone.typinggame.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
public class LeaderboardServiceImpl implements LeaderboardService {

    @Autowired
    private UserRepository userRepository;


    @Override
    public List<User> fetchTopPlayers() {
        List<User> topTwentyUsers = getPlayersSortedBySpeed().subList(0, 20);
        return topTwentyUsers;
    }

    private List<User> getPlayersSortedBySpeed() {
        List<User> users = userRepository.findAll();
        Collections.sort(users, (user1, user2) -> {
            if (user1.getUserStats().getAverageSpeed() == user2.getUserStats().getAverageSpeed()) return user1.getUsername().compareToIgnoreCase(user2.getUsername());
            return Double.compare(user1.getUserStats().getAverageSpeed(), user2.getUserStats().getAverageSpeed());
        });
        return users;
    }
}
