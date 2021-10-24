package com.teamone.typinggame.configuration;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
@Data
public class GameConfig {

    @Value("${game.max-players}")
    private int maxNumOfPlayers;

}
