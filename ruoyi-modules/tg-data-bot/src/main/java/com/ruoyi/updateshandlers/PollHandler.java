package com.ruoyi.updateshandlers;

import com.ruoyi.commands.StartRecvMsgCommand;
import com.ruoyi.commands.StopRecvMsgCommand;
import com.ruoyi.config.properties.BotConfig;
import com.ruoyi.utils.ThreadUtil;
import lombok.extern.slf4j.Slf4j;
import com.ruoyi.commands.StartCommand;
import org.apache.commons.collections4.CollectionUtils;
import org.telegram.telegrambots.extensions.bots.commandbot.CommandLongPollingTelegramBot;
import org.telegram.telegrambots.extensions.bots.commandbot.commands.IBotCommand;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.api.objects.chat.Chat;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * This handler mainly works with commands to demonstrate the Commands feature of the API
 *
 * @author Timo Schulz (Mit0x2)
 */
@Slf4j
public class PollHandler extends CommandLongPollingTelegramBot implements ISetCommand{

    private CommonHandler commonHandler;

    private BotConfig botConfig;

    private TelegramClient telegramClient;

    /**
     * Constructor.
     */
    public PollHandler(TelegramClient client, BotConfig botConfig, CommonHandler commonHandler) {
        super(client, true, botConfig::getUser);

        this.commonHandler = commonHandler;
        this.botConfig = botConfig;
        this.telegramClient = client;
        StartCommand startCommand = new StartCommand(botConfig);
        StartRecvMsgCommand startRecvMsgCommand = new StartRecvMsgCommand(botConfig);
        StopRecvMsgCommand stopRecvMsgCommand = new StopRecvMsgCommand(botConfig);


        register(startCommand);

        if(!"8571189674".equals(botConfig.getBotId())) {//这里先写死
            register(startRecvMsgCommand);
            register(stopRecvMsgCommand);
        }

        setCommands(null);
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

    public void setCommands(List<org.telegram.telegrambots.meta.api.objects.commands.BotCommand> cmds) {

        Collection<IBotCommand> registeredCommands = getRegisteredCommands();
        if(cmds == null) {
            cmds = new ArrayList<>();
        }
        List<BotCommand> finalCmds = cmds;
        registeredCommands.forEach(cmd -> {
            finalCmds.add(new BotCommand(cmd.getCommandIdentifier(), cmd.getDescription()));
        });

        try {
            telegramClient.execute(SetMyCommands.builder()
                    .commands(finalCmds).build());
        } catch (TelegramApiException e) {
            log.error("批量注册命令失败", e.getMessage());
        }
    }

    public void setCommand (String command, String descp) {
        org.telegram.telegrambots.meta.api.objects.commands.BotCommand cmd = new org.telegram.telegrambots.meta.api.objects.commands.BotCommand(command, descp);
        try {
            telegramClient.execute(SetMyCommands.builder()
                    .command(cmd).build());
        } catch (TelegramApiException e) {
            log.error("注册命令失败", e.getMessage());
        }
    }
}