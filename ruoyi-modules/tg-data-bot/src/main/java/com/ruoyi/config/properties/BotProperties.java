package com.ruoyi.config.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.data.annotation.Transient;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Component
@ConfigurationProperties(prefix = "bot")
@Data
public class BotProperties {

    private String model;

    @Transient
    private List<BotConfig> botConfigs;

    private String webhookUrl;

    public void addBot(Long botId, String botToken, String botUser) {
        if (botConfigs == null) {
            botConfigs = new ArrayList<>();
        }
        botConfigs.removeIf(botConfig -> botConfig.getBotId().equals(botId.toString()));

        BotConfig botConfig = new BotConfig();
        botConfig.setToken(botToken);
        botConfig.setUser(botUser);
        botConfigs.add(botConfig);
    }
}