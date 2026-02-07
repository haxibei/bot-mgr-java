package com.ruoyi.config;

import com.ruoyi.config.properties.BotConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.ruoyi.config.properties.BotProperties;
import com.ruoyi.updateshandlers.CommonHandler;
import com.ruoyi.updateshandlers.WebHookHandler;

import java.util.List;

/**
 * @author ruoyi
 * @since 1.0
 */
@Configuration
@ConditionalOnProperty(name = "bot.model", havingValue = "webhook")
public class WebhookConfig {

	@Autowired
	private BotProperties botProperties;

	@Autowired
	private BotClientConfig botClientConfig;

	@Autowired
	private CommonHandler commonHandler;

	@Bean
	public BotHandlerConfig getBotHandlerConfig() {

		BotHandlerConfig botHandlerConfig = new BotHandlerConfig();

		List<BotConfig> botConfigs = botProperties.getBotConfigs();

		for(BotConfig botConfig : botConfigs) {
			WebHookHandler handler = new WebHookHandler(botClientConfig.getClient(botConfig.getBotId()), botConfig, botProperties.getWebhookUrl(), commonHandler);
			botHandlerConfig.putHandler(botConfig.getBotId(), handler);
		}
		return botHandlerConfig;
	}
}
