package com.ruoyi.config;

import org.telegram.telegrambots.extensions.bots.commandbot.CommandBot;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.util.HashMap;
import java.util.Map;

/**
 * @author ruoyi
 * @since 1.0
 */
public class BotClientConfig {

	private Map<String/*botId*/, TelegramClient> clientMap = new HashMap<String, TelegramClient>();

	public void putClient(String botId, TelegramClient client) {
		clientMap.put(botId, client);
	}

	public TelegramClient getClient(String botId) {
		return clientMap.get(botId);
	}

}
