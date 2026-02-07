package com.ruoyi.config;


import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.util.HashMap;
import java.util.Map;

/**
 * @author ruoyi
 * @since 1.0
 */
public class BotClient {

	private static Map<String/*botId*/, TelegramClient> clientMap = new HashMap<String, TelegramClient>();

	public static void putClient(String botId, TelegramClient client) {
		clientMap.put(botId, client);
	}

	public static TelegramClient getClient(String botId) {
		return clientMap.get(botId);
	}

	public static TelegramClient getClient(String botId, String botToken) {
		TelegramClient client = clientMap.get(botId);
		if(client == null) {
			client = new OkHttpTelegramClient(botToken);
			clientMap.put(botId, client);
		}
		return client;
	}

}
