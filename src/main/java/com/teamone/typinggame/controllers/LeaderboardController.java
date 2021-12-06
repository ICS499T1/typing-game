package com.teamone.typinggame.controllers;

import com.teamone.typinggame.models.Leaderboard;
import com.teamone.typinggame.services.game.LeaderboardServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/leaderboard")
public class LeaderboardController {

    @Autowired
    private LeaderboardServiceImpl leaderboardService;

    /**
     * Returns TOP-20 users.
     * @return Leaderboard - top users.
     */
    @CrossOrigin
    @GetMapping
    public Leaderboard getTopUsers() {
        return leaderboardService.fetchTopStats();
    }

}
