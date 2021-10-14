package com.teamone.typinggame.controllers;

import com.teamone.typinggame.models.Stats;
import com.teamone.typinggame.services.StatsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value="/stats")
public class StatsController {
    public final StatsService statsService;

    @Autowired
    public StatsController(StatsService statsService) {
        this.statsService = statsService;
    }

    @PutMapping("/multi")
    public Stats updateStatsMultiplayer(@RequestParam(name="id") Long statsId, @RequestParam(name="speed") Double speed, @RequestParam(name="victory") Boolean victory) {
        return statsService.updateStatsMultiplayer(statsId, speed, victory);
    }

    @PutMapping("/single")
    public Stats updateStatsSinglePlayer(@RequestParam(name="id") Long statsId, @RequestParam(name="speed") Double speed) {
        return statsService.updateStatsSinglePlayer(statsId, speed);
    }


}
