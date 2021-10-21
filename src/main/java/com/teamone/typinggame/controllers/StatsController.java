package com.teamone.typinggame.controllers;

import com.teamone.typinggame.models.Stats;
import com.teamone.typinggame.services.StatsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value="/stats")
public class StatsController {
    public final StatsServiceImpl statsServiceImpl;

    @Autowired
    public StatsController(StatsServiceImpl statsServiceImpl) {
        this.statsServiceImpl = statsServiceImpl;
    }

    @PutMapping("/multi")
    public Stats updateStatsMultiplayer(@RequestParam(name="id") Long statsId, @RequestParam(name="speed") Double speed, @RequestParam(name="victory") Boolean victory) {
        return statsServiceImpl.updateStatsMultiplayer(statsId, speed, victory);
    }

    @PutMapping("/single")
    public Stats updateStatsSinglePlayer(@RequestParam(name="id") Long statsId, @RequestParam(name="speed") Double speed) {
        return statsServiceImpl.updateStatsSinglePlayer(statsId, speed);
    }


}
