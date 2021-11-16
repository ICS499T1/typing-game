package com.teamone.typinggame.services.game;

import com.teamone.typinggame.models.Leaderboard;
import com.teamone.typinggame.models.LeaderboardRow;
import com.teamone.typinggame.models.User;
import com.teamone.typinggame.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Service
public class LeaderboardServiceImpl implements LeaderboardService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public Leaderboard fetchTopStats() {
        List<User> fastestUsers = userRepository.fetchTopTwentyUsers();
        List<User> fastestRacers = userRepository.fetchTopTwentyRaces();
        List<LeaderboardRow> fastestPlayers = new ArrayList<>(20);
        List<LeaderboardRow> fastestRaces = new ArrayList<>(20);
        fastestUsers.forEach(user -> {
            fastestPlayers.add(new LeaderboardRow(user.getUsername(), user.getUserStats().getAverageSpeed()));
        });

        fastestRacers.forEach(user -> {
            fastestRaces.add(new LeaderboardRow(user.getUsername(), user.getUserStats().getBestRaceSpeed()));
        });

        return new Leaderboard(fastestPlayers, fastestRaces);
    }
}
