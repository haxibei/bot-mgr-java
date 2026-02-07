package com.ruoyi.handlers;

import com.alibaba.fastjson2.JSON;
import com.ruoyi.common.redis.constant.DefaultJedisKeyNS;
import com.ruoyi.common.redis.service.RedisService;
import com.ruoyi.utils.ThreadUtil;
import lombok.NonNull;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.ruoyi.constant.BtnCallCmd;
import com.ruoyi.db.domain.Commandusers;
import com.ruoyi.db.service.ICommandusersService;
import com.ruoyi.handlers.model.CmdBean;

@Component
public class CmdUtil {

    @Autowired
    private RedisService redisService;

    @Autowired
    private ICommandusersService commandusersService;

    public void putCmd(BtnCallCmd cmd, String chatId) {
        putCmd(cmd, chatId, null);
    }

    public void putCmd(BtnCallCmd cmd, String chatId, String param) {
        redisService.setCacheObject(DefaultJedisKeyNS.tg_wait_input, buildKey(chatId), new CmdBean(cmd.name(), param));
    }

    private String buildKey(String chatId) {
        String botId = ThreadUtil.getBotId();
        return chatId + "_" + botId;
    }

    public void clearCmd(String chatId) {
        redisService.deleteObject(DefaultJedisKeyNS.tg_wait_input, buildKey(chatId));
    }

    public CmdBean getCmd(String chatId) {
        Object cacheObject = redisService.getCacheObject(DefaultJedisKeyNS.tg_wait_input, buildKey(chatId));
        return cacheObject == null?null:(CmdBean) cacheObject;
    }

    public boolean getUserStateForCommandsBot(@NonNull Long id) {
        Commandusers query = new Commandusers();
        query.setBotId(ThreadUtil.getBotId());
        query.setUserId(id);
        Commandusers user = commandusersService.getOne(commandusersService.getBaseWrapper(query));

        return user != null && user.getStatus() == 1;
    }

    public boolean setUserStateForCommandsBot(@NonNull Long id, String userName, String nickName, String recommendUid) {
        Commandusers user = new Commandusers();
        user.setUserId(id);
        user.setUserName(userName);
        user.setNickName(nickName);
        user.setStatus(1L);
        user.setBotId(ThreadUtil.getBotId());
        user.setRecommendUid(StringUtils.isBlank(recommendUid)?0:Long.parseLong(recommendUid));

        return commandusersService.insertOrUpdate(user);
    }

    public boolean getLockState(@NonNull Long userId) {
        Integer lock = redisService.getCacheObject(DefaultJedisKeyNS.tg_msg_lock, buildKey(userId.toString()));
        return lock != null;
    }

    public void lockedTgMsg(@NonNull Long userId) {
        redisService.setCacheObject(DefaultJedisKeyNS.tg_msg_lock, buildKey(userId.toString()), 1);
    }

    public void unLockedTgMsg(@NonNull Long userId) {
        redisService.deleteObject(DefaultJedisKeyNS.tg_msg_lock, buildKey(userId.toString()));
    }
}
