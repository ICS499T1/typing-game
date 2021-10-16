package com.teamone.typinggame.controllers;

import com.teamone.typinggame.models.KeyStats;
import com.teamone.typinggame.models.User;
import com.teamone.typinggame.services.KeyStatsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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

    @PostMapping("/getuserkeys")
    public List<KeyStats> getUserKeys(@RequestBody User user) {
        return keyStatsService.getUserKeys(user);
    }


}
