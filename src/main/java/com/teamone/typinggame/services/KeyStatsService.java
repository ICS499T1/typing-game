package com.teamone.typinggame.services;

import com.teamone.typinggame.models.KeyStats;
import com.teamone.typinggame.repositories.KeyStatsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class KeyStatsService {
    private final KeyStatsRepository keyStatsRepository;

    @Autowired
    public KeyStatsService(KeyStatsRepository keyStatsRepository) {
        this.keyStatsRepository = keyStatsRepository;
    }

    public List<KeyStats> test(){
        List<KeyStats> list = keyStatsRepository.findByUser(2L);
        System.out.println(list.get(0));
        return list;
    }

}
