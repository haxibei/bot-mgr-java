package com.ruoyi.listener;

import com.alibaba.fastjson2.JSON;
import com.ruoyi.db.domain.GroupInfo;
import com.ruoyi.db.service.IGroupInfoService;
import com.ruoyi.model.TgGroupInfoContent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@Slf4j
public class GroupInfoRedisSubscriber implements MessageListener {

    @Autowired
    private IGroupInfoService groupInfoService;


    @Override
    public void onMessage(Message message, byte[] pattern) {
        String channel = new String(message.getChannel());
        String channelMsg = new String(message.getBody());

        log.info("收到频道 [" + channel + "] 的消息: " + channelMsg);

        TgGroupInfoContent msgContent = JSON.parseObject(channelMsg, TgGroupInfoContent.class);

        GroupInfo groupInfo = new GroupInfo();
        groupInfo.setTgId(msgContent.getTgId());
        groupInfo.setGroupId(msgContent.getGroupId());
        groupInfo.setUserName(msgContent.getUserName());
        groupInfo.setGroupTitle(msgContent.getGroupTitle());
        groupInfo.setLink(msgContent.getLink());
        groupInfo.setCreateTime(LocalDateTime.now());
        groupInfo.setUpdateTime(LocalDateTime.now());

        groupInfoService.insertOrUpdate(groupInfo);
    }
}