package com.ruoyi.config;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import java.util.TimeZone;

/**
 * @author ruoyi
 * @since 1.0
 */
@Configuration
public class AppConfig {

	@Bean("appRestTemplate")
	public RestTemplate getRestTemplate() {
		return new RestTemplate();
	}

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
