package com.ruoyi.config;

import com.ruoyi.config.properties.BotConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.ruoyi.config.properties.BotProperties;
import org.telegram.telegrambots.extensions.bots.commandbot.CommandBot;
import org.telegram.telegrambots.longpolling.TelegramBotsLongPollingApplication;
import com.ruoyi.updateshandlers.PollHandler;
import com.ruoyi.updateshandlers.CommonHandler;

import java.util.List;

/**
 * @author ruoyi
 * @since 1.0
 */
@Configuration
@ConditionalOnProperty(name = "bot.model", havingValue = "poll")
@Slf4j
public class PollConfig {

	@Autowired
	private BotProperties botProperties;

	@Autowired
	private CommonHandler commonHandler;

	@Autowired
	private BotClientConfig botClientConfig;

	@Bean
	public BotHandlerConfig getBotHandlerConfig() {

		BotHandlerConfig botHandlerConfig = new BotHandlerConfig();

		List<BotConfig> botConfigs = botProperties.getBotConfigs();

		for(BotConfig botConfig : botConfigs) {
			PollHandler pollHandler = new PollHandler(botClientConfig.getClient(botConfig.getBotId()), botConfig, commonHandler);

			botHandlerConfig.putHandler(botConfig.getBotId(), pollHandler);
		}
		return botHandlerConfig;
	}

	@Bean(destroyMethod = "close")
	public TelegramBotsLongPollingApplication botsApplication() {
		TelegramBotsLongPollingApplication botsApplication = new TelegramBotsLongPollingApplication();
		try {
			BotHandlerConfig config = getBotHandlerConfig();
			for (CommandBot handler : config.getHandlers()) {
				PollHandler pollHandler = (PollHandler) handler;
				botsApplication.registerBot(((PollHandler) handler).getBotConfig().getToken(), pollHandler);
			}

		} catch (Exception e) {
			log.error("Error registering bot", e);
		}
		return botsApplication;
	}
}
