package com.ruoyi.services;

import com.ruoyi.common.core.utils.StringUtils;
import com.ruoyi.common.redis.constant.DefaultJedisKeyNS;
import com.ruoyi.common.redis.service.RedisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class BotConfigService {

    @Autowired
    private RedisService redisService;

    public String getKefu() {
        return redisService.getCacheObject(DefaultJedisKeyNS.global_config, "bot_kefu");
    }

    public String getKefu2Btn() {
        return getKefu();
    }

    public long getOrderExpireSecond() {
        Long expireData = redisService.getCacheObject(DefaultJedisKeyNS.global_config, "");
        return expireData == null?10 * 60: expireData;
    }

    public String getMgrUserId() {
        String uid = redisService.getCacheObject(DefaultJedisKeyNS.global_config, "bot_mgr");
        return StringUtils.isBlank(uid)?"7845216949":uid;
    }

    public int getRemotePageSize() {
        Integer pageSize = redisService.getCacheObject(DefaultJedisKeyNS.global_config, "remote_page_size");
        return (pageSize == null)? 9999:pageSize;
    }
}
