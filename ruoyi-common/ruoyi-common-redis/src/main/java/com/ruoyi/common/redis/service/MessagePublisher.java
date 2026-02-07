package com.ruoyi.common.redis.service;

import com.ruoyi.common.core.constant.ServiceNameConstants;
import org.springframework.data.redis.listener.ChannelTopic;

import java.util.HashMap;
import java.util.Map;

public enum MessagePublisher {

    adm_db("/msg/db/adm", "广告模块"),

    system_db("/msg/db/system", "系统模块"),

    ;

    private String channel;

    private String descp;

    private MessagePublisher(String channel, String descp) {
        this.channel = channel;
        this.descp = descp;
    }

    public static MessagePublisher get(String applicationName) {
        MessagePublisher publisher = null;
        switch (applicationName) {
            case ServiceNameConstants.ADM_SERVICE: publisher = adm_db;break;
            case ServiceNameConstants.SYSTEM_SERVICE: publisher = system_db;break;
        }

        return publisher;
    }

    public ChannelTopic getChannelTopic() {
        ChannelTopic channelTopic = channels.get(this.name());

        if(channelTopic == null) {
            channelTopic = new ChannelTopic(this.getChannel());
            channels.put(this.name(), channelTopic);
        }
        return channelTopic;
    }

    public String getChannel() {
        return channel;
    }

    public String getDescp() {
        return descp;
    }

    private static Map<String, ChannelTopic> channels = new HashMap<>();
}
