package com.ruoyi.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;
import com.ruoyi.config.properties.BotProperties;
import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient;

import java.util.List;

/**
 * @author ruoyi
 * @since 1.0
 */
@Configuration
public class AppConfig {

	@Autowired
	private BotProperties botProperties;

	@Bean("appRestTemplate")
	public RestTemplate getRestTemplate() {
		// 定义请求工厂
		HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory();
		factory.setConnectTimeout(10000);   // 连接超时：5秒
		factory.setReadTimeout(10000);     // 读取超时：10秒
		factory.setConnectionRequestTimeout(3000); // 从连接池获取连接超时

		return new RestTemplate(factory);
	}

	@Bean
	public BotClientConfig getBotConfig() {

		BotClientConfig botClientConfig = new BotClientConfig();

		List<com.ruoyi.config.properties.BotConfig> botConfigs = botProperties.getBotConfigs();

		for(com.ruoyi.config.properties.BotConfig botConfig : botConfigs) {
			botClientConfig.putClient(botConfig.getBotId(), new OkHttpTelegramClient(botConfig.getToken()));
		}
		return botClientConfig;
	}

}
