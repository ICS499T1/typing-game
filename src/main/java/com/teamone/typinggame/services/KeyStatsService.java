package com.teamone.typinggame.services;

import com.teamone.typinggame.models.KeyStats;
import com.teamone.typinggame.models.User;
import com.teamone.typinggame.repositories.KeyStatsRepository;
import com.teamone.typinggame.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class KeyStatsService {
    private final KeyStatsRepository keyStatsRepository;
    private final UserRepository userRepository;

    @Autowired
    public KeyStatsService(KeyStatsRepository keyStatsRepository, UserRepository userRepository) {
        this.keyStatsRepository = keyStatsRepository;
        this.userRepository = userRepository;
    }

    public List<KeyStats> test(){
        User user = userRepository.getById(2L);
        List<KeyStats> list = keyStatsRepository.findByUser(user);
        System.out.println(list.get(0));
        return list;
    }

}
