package com.ruoyi.updateshandlers;

import com.ruoyi.commands.StartRecvMsgCommand;
import com.ruoyi.commands.StopRecvMsgCommand;
import com.ruoyi.config.properties.BotConfig;
import com.ruoyi.utils.ThreadUtil;
import lombok.extern.slf4j.Slf4j;
import com.ruoyi.commands.StartCommand;
import com.ruoyi.config.properties.BotProperties;
import org.telegram.telegrambots.extensions.bots.commandbot.TelegramWebhookCommandBot;
import org.telegram.telegrambots.extensions.bots.commandbot.commands.IBotCommand;
import org.telegram.telegrambots.meta.api.methods.botapimethods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.commands.DeleteMyCommands;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.methods.updates.DeleteWebhook;
import org.telegram.telegrambots.meta.api.methods.updates.SetWebhook;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import org.telegram.telegrambots.meta.api.objects.message.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * @author pithera
 * @version 1.0
 * Simple Webhook example
 */
@Slf4j
public class WebHookHandler extends TelegramWebhookCommandBot implements ISetCommand {

    private TelegramClient telegramClient;

    private CommonHandler commonHandler;

    private String webhookUrl;

    private BotConfig botConfig;

    public WebHookHandler(TelegramClient telegramClient, BotConfig botConfig, String webhookUrl, CommonHandler commonHandler) {
        super(telegramClient, true, () -> botConfig.getUser(), null, null, null);
        this.telegramClient = telegramClient;
        this.commonHandler = commonHandler;
        this.webhookUrl = webhookUrl;
        this.botConfig = botConfig;

        StartCommand startCommand = new StartCommand(botConfig);
        StartRecvMsgCommand startRecvMsgCommand = new StartRecvMsgCommand(botConfig);
        StopRecvMsgCommand stopRecvMsgCommand = new StopRecvMsgCommand(botConfig);


        register(startCommand);

        if(!"8571189674".equals(botConfig.getBotId())) {//这里先写死
            register(startRecvMsgCommand);
            register(stopRecvMsgCommand);
        }


        setCommands(null);

//        registerDefaultAction((telegramClient, message) -> {
//            SendMessage commandUnknownMessage = new SendMessage(String.valueOf(message.getChatId()),
//                    "The command '" + message.getText() + "' is not known by this bot. Here comes some help " + Emoji.AMBULANCE);
//            try {
//                telegramClient.execute(commandUnknownMessage);
//            } catch (TelegramApiException e) {
//                log.error("Error sending message in commands bot", e);
//            }
//        });
    }

    @Override
    public void runDeleteWebhook() {
        try {
            telegramClient.execute(new DeleteWebhook());
        } catch (TelegramApiException e) {
            log.info("Error deleting webhook");
        }
    }

    @Override
    public void runSetWebhook() {
        try {
            String hook = webhookUrl + getBotPath();
            log.info("Setting webhook {}", hook);
            telegramClient.execute(SetWebhook
                    .builder()
                    .url(hook)
                    .build());
        } catch (TelegramApiException e) {
            log.info("Error setting webhook");
        }
    }

    @Override
    public String getBotPath() {
        return "/tg/botRecvMsg/" + botConfig.getBotId(); //arbitrary path to deliver updates on, username is an example.
    }

    @Override
    public void processNonCommandUpdate(Update update) {
        commonHandler.processMsg(telegramClient, update);
    }

    @Override
    public void processInvalidCommandUpdate(Update update) {
        super.processInvalidCommandUpdate(update);
    }

    @Override
    public boolean filter(Message message) {
        return super.filter(message);
    }

    public Serializable execute(BotApiMethod msg) throws TelegramApiException {
        return telegramClient.execute(msg);
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
}
