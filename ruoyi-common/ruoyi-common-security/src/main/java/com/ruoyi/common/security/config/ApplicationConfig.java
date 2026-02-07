package com.ruoyi.common.security.config;

import java.util.TimeZone;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;

/**
 * 系统配置
 *
 * @author ruoyi
 */
public class ApplicationConfig
{
    /**
     * 时区配置
     */
    @Bean
    public Jackson2ObjectMapperBuilderCustomizer jacksonObjectMapperCustomization()
    {
        return builder -> {
            // 序列化时字段值为 null 时不输出
            builder.timeZone(TimeZone.getDefault());
            builder.serializationInclusion(JsonInclude.Include.NON_NULL);
            builder.timeZone(TimeZone.getDefault())
                    .serializerByType(Long.class, new ToStringSerializer());
        };
    }
}
