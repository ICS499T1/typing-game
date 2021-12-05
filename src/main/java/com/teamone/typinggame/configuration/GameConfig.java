package com.teamone.typinggame.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Configuration
@ConfigurationProperties(prefix = "game")
public class GameConfig {

    private int maxPlayers;
    private String secret;
    private Map<String, String> token;
    private Map<String, String> auth;

    public int getMaxPlayers() {
        return maxPlayers;
    }

    public void setMaxPlayers(int maxPlayers) {
        this.maxPlayers = maxPlayers;
    }

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    public String getTokenProperty(String property) {
        if (token.containsKey(property)) return token.get(property);
        return null;
    }

    public String getAuthProperty(String property) {
        if (auth.containsKey(property)) return auth.get(property);
        return null;
    }

}
