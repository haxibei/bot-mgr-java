package com.ruoyi.common.redis.service;

import com.ruoyi.common.core.domain.DbModifyMsg;
import com.ruoyi.common.core.utils.SpringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;

@Slf4j
public class AbstractDbMsgReceiver implements MessageListener {

    @Override
    public void onMessage(Message message, byte[] bytes) {
        RedisService redisService = SpringUtils.getBean("redisService");
        RedisTemplate redisTemplate = redisService.getRedisTemplate();

        RedisSerializer<DbModifyMsg> valueSerializer = redisTemplate.getValueSerializer();
        DbModifyMsg deserialize = valueSerializer.deserialize(message.getBody());

        doMsg(message, deserialize);
    }

    protected void doMsg(Message message, DbModifyMsg param) {
        log.info("收到消息" + param);

    }


}
