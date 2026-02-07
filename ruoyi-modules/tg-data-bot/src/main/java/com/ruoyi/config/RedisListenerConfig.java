//package com.ruoyi.config;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.data.redis.connection.RedisConnectionFactory;
//import org.springframework.data.redis.listener.ChannelTopic;
//import org.springframework.data.redis.listener.RedisMessageListenerContainer;
//import com.ruoyi.constant.RedisChannel;
//
//@Configuration
//public class RedisListenerConfig {
//
//    @Autowired
//    private RedisSubscriber redisSubscriber; // 注入上面的监听器
//
//    @Bean
//    public RedisMessageListenerContainer container(RedisConnectionFactory factory) {
//        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
//        container.setConnectionFactory(factory);
//        // 订阅名为 "testChannel" 的频道
//        container.addMessageListener(redisSubscriber, new ChannelTopic(RedisChannel.tgTronChannel));
//        return container;
//    }
//}