package com.teamone.typinggame.controllers;

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
    public void updateStatsMultiplayer(@RequestParam(name="id") Integer statsId, @RequestParam(name="speed") Double speed, @RequestParam(name="victory") Boolean victory) {
        System.out.println(statsId + " " + speed + " " + victory);
    }

    @PutMapping("/single")
    public void updateStatsSinglePlayer(@RequestParam(name="id") Integer statsId, @RequestParam(name="speed") Double speed) {
        
    }


}
