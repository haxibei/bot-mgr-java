package com.ruoyi.config;

import org.telegram.telegrambots.extensions.bots.commandbot.CommandBot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author ruoyi
 * @since 1.0
 */
public class BotHandlerConfig {

	private Map<String/*botId*/, CommandBot> botHandlerMap = new HashMap<String, CommandBot>();


	public void putHandler(String botId, CommandBot handler) {
		botHandlerMap.put(botId, handler);
	}

	public CommandBot getHandler(String botId) {
		return botHandlerMap.get(botId);
	}

	public List<CommandBot> getHandlers() {
		return new ArrayList<CommandBot>(botHandlerMap.values());
	}
}
