package com.ruoyi.config.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@ConfigurationProperties(prefix = "bot")
@Data
public class BotProperties {

    private String model;

    private List<BotConfig> botConfigs;

    private String webhookUrl;
}