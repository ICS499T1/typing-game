package com.teamone.typinggame.controllers;

import com.teamone.typinggame.models.KeyStats;
import com.teamone.typinggame.models.User;
import com.teamone.typinggame.services.KeyStatsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value="/keyStats")
public class KeyStatsController {
    public final KeyStatsServiceImpl keyStatsServiceImpl;

    @Autowired
    public KeyStatsController(KeyStatsServiceImpl keyStatsServiceImpl) {
        this.keyStatsServiceImpl = keyStatsServiceImpl;
    }

//    @GetMapping("/test")
//    public List<KeyStats> test(){
//        return keyStatsServiceImpl.test();
//    }

    @PostMapping("/getuserkeys")
    public List<KeyStats> getUserKeys(@RequestBody User user) {
        return keyStatsServiceImpl.getUserKeys(user);
    }


}
