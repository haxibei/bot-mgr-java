package com.ruoyi.config.properties;

import lombok.Data;

@Data
public class BotConfig {

    private String token;

    private String user;

    public String getBotId() {
        return token.split(":")[0];
    }

}
