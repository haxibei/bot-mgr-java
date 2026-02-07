package com.ruoyi.config;

import com.ruoyi.constant.RedisChannel;
import com.ruoyi.listener.GroupInfoRedisSubscriber;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;

@Configuration
public class RedisListenerConfig {

    @Autowired
    private GroupInfoRedisSubscriber groupInfoRedisSubscriber; // 注入上面的监听器

    @Bean
    public RedisMessageListenerContainer container(RedisConnectionFactory factory) {
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(factory);
        container.addMessageListener(groupInfoRedisSubscriber, new ChannelTopic(RedisChannel.tgGroupInfoChannel));
        return container;
    }
}