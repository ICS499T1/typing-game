package com.teamone.typinggame.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "game")
public class GameConfig {

    private String secret;
    private String textGeneratorUrl;

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    public String getTextGeneratorUrl() {
        return textGeneratorUrl;
    }

    public void setTextGeneratorUrl(String textGeneratorUrl) {
        this.textGeneratorUrl = textGeneratorUrl;
    }
}
