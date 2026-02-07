package com.ruoyi.updateshandlers;

import com.ruoyi.commands.StartRecvMsgCommand;
import com.ruoyi.commands.StopRecvMsgCommand;
import com.ruoyi.config.properties.BotConfig;
import lombok.extern.slf4j.Slf4j;
import com.ruoyi.commands.StartCommand;
import com.ruoyi.config.properties.BotProperties;
import org.telegram.telegrambots.extensions.bots.commandbot.TelegramWebhookCommandBot;
import org.telegram.telegrambots.meta.api.methods.botapimethods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.updates.DeleteWebhook;
import org.telegram.telegrambots.meta.api.methods.updates.SetWebhook;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.message.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.io.Serializable;

/**
 * @author pithera
 * @version 1.0
 * Simple Webhook example
 */
@Slf4j
public class WebHookHandler extends TelegramWebhookCommandBot {

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

        register(new StartCommand(botConfig));
        register(new StartRecvMsgCommand(botConfig));
        register(new StopRecvMsgCommand(botConfig));

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

}
