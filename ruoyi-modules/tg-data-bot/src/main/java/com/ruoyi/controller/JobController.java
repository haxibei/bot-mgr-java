package com.ruoyi.controller;

import com.ruoyi.common.core.utils.StringUtils;
import com.ruoyi.common.redis.constant.DefaultJedisKeyNS;
import com.ruoyi.common.redis.service.RedisService;
import com.ruoyi.config.BotClientConfig;
import com.ruoyi.db.service.IAgentBindInfoService;
import com.ruoyi.db.service.IDataInfoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.util.List;

@RestController
@RequestMapping("/job")
@Slf4j
public class JobController {

    @Autowired
    private IDataInfoService dataInfoService;

    @Autowired
    private IAgentBindInfoService agentBindInfoService;

    @Autowired
    private RedisService redisService;

    @Autowired
    private BotClientConfig botClientConfig;


    @GetMapping("/spiderData")
    public String spiderData(String date) {
        log.info("spiderData: {}", date);
        try {
            dataInfoService.spiderData(date);
        }catch (Exception e) {
            log.error(e.getMessage(), e);
            return "fail";
        }
        return "success";
    }

    @GetMapping("/refreshTk")
    public String refreshTk(String date) {
        log.info("refreshTk: {}", date);
        try {
            dataInfoService.refreshTk(date);
        }catch (Exception e) {
            log.error(e.getMessage(), e);
            return "fail";
        }
        return "success";
    }

    @GetMapping("/pushDataMsg")
    public String pushDataMsg(String date) {
        log.info("pushDataMsg: {}", date);
        try {
            dataInfoService.pushDataMsg(date);
        }catch (Exception e) {
            log.error(e.getMessage(), e);
            return "fail";
        }
        return "success";
    }

    @GetMapping("/sendToGroupMsg")
    public String sendToGroupMsg(String botId, String msg) {
        log.info("sendToGroupMsg: {}  {}", botId, msg);
        try {
            List<String> groupIds = agentBindInfoService.selectAllGroup(botId);

            if(StringUtils.isBlank(msg)) {
                msg = redisService.getCacheObject(DefaultJedisKeyNS.global_config, "send_group_msg");
            }else {
                msg = redisService.getCacheObject(DefaultJedisKeyNS.global_config, msg);
            }

            for(String groupId : groupIds) {
                SendMessage sendMessage = new SendMessage(groupId, msg);
                try {
                    TelegramClient client = botClientConfig.getClient(botId);
                    client.execute(sendMessage);
                } catch (TelegramApiException e) {
                    log.error("sendToGroupMsg to group msg err", e);
                }
            }
        }catch (Exception e) {
            log.error(e.getMessage(), e);
            return "fail";
        }
        return "success";
    }
}


