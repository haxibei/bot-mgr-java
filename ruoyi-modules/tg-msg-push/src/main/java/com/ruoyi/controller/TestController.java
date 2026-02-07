package com.ruoyi.controller;

import com.ruoyi.common.redis.service.RedisService;
import com.ruoyi.constant.RedisChannel;
import com.ruoyi.db.service.ITgInfoService;
import com.ruoyi.model.TgMsgContent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/test")
@Slf4j
public class TestController {

    @Autowired
    private RedisService redisService;



    @GetMapping("/sendTgMsg")
    public String spiderData(String msg) {
        log.info("test sendTgMsg: {}", msg);
        try {
            TgMsgContent tgMsgContent = new TgMsgContent();
            tgMsgContent.setChatId("1111");
            tgMsgContent.setMsg("asdasdsad");
            tgMsgContent.setType(0);

            redisService.convertAndSend(RedisChannel.tgGroupMsgChannel, tgMsgContent);
        }catch (Exception e) {
            log.error(e.getMessage(), e);
            return "fail";
        }
        return "success";
    }
}


