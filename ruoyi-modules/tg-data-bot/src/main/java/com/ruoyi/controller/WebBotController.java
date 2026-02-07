package com.ruoyi.controller;

import com.alibaba.fastjson2.JSON;
import com.ruoyi.config.BotHandlerConfig;
import com.ruoyi.utils.ThreadUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.web.bind.annotation.*;
import org.telegram.telegrambots.extensions.bots.commandbot.CommandBot;
import org.telegram.telegrambots.meta.api.objects.Update;
import com.ruoyi.updateshandlers.WebHookHandler;

@RestController
@RequestMapping("/tg")
@Slf4j
@ConditionalOnProperty(name = "bot.model", havingValue = "webhook")
public class WebBotController {

    @Autowired
    private BotHandlerConfig botHandlerConfig;

    @PostMapping("/botRecvMsg/{botId}")
    public String botRecvMsg(@RequestBody Update update, @PathVariable("botId") String botId) {
        log.info("botRecvMsg update: {} {}", botId, JSON.toJSON(update));
        try {
            WebHookHandler handler = (WebHookHandler) botHandlerConfig.getHandler(botId);
            ThreadUtil.clear();
            ThreadUtil.setBotId(botId);
            handler.consumeUpdate(update);
        }catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return "";
    }
}


