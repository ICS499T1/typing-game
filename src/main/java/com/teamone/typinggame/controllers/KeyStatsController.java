package com.teamone.typinggame.controllers;

import com.teamone.typinggame.models.KeyStats;
import com.teamone.typinggame.services.KeyStatsService;
import com.teamone.typinggame.services.StatsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(value="/keyStats")
public class KeyStatsController {
    public final KeyStatsService keyStatsService;

    @Autowired
    public KeyStatsController(KeyStatsService keyStatsService) {
        this.keyStatsService = keyStatsService;
    }

    @GetMapping("/test")
    public List<KeyStats> test(){
        return keyStatsService.test();
    }


}
