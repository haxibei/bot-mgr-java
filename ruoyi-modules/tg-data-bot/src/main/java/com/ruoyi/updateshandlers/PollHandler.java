package com.ruoyi.updateshandlers;

import com.ruoyi.commands.StartRecvMsgCommand;
import com.ruoyi.commands.StopRecvMsgCommand;
import com.ruoyi.config.properties.BotConfig;
import com.ruoyi.utils.ThreadUtil;
import lombok.extern.slf4j.Slf4j;
import com.ruoyi.commands.StartCommand;
import org.telegram.telegrambots.extensions.bots.commandbot.CommandLongPollingTelegramBot;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.generics.TelegramClient;

/**
 * This handler mainly works with commands to demonstrate the Commands feature of the API
 *
 * @author Timo Schulz (Mit0x2)
 */
@Slf4j
public class PollHandler extends CommandLongPollingTelegramBot {

    private CommonHandler commonHandler;

    private BotConfig botConfig;

    /**
     * Constructor.
     */
    public PollHandler(TelegramClient client, BotConfig botConfig, CommonHandler commonHandler) {
        super(client, true, botConfig::getUser);

        this.commonHandler = commonHandler;
        this.botConfig = botConfig;
        StartCommand startCommand = new StartCommand(botConfig);
        register(startCommand);
        register(new StartRecvMsgCommand(botConfig));
        register(new StopRecvMsgCommand(botConfig));
    }

    @Override
    public void processNonCommandUpdate(Update update) {
        ThreadUtil.clear();
        ThreadUtil.setBotId(botConfig.getBotId());
        commonHandler.processMsg(telegramClient, update);
    }

    public BotConfig getBotConfig() {
        return botConfig;
    }
}