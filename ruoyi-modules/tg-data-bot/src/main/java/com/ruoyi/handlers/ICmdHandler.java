package com.ruoyi.handlers;

import com.ruoyi.constant.BtnCallCmd;
import org.telegram.telegrambots.meta.api.methods.botapimethods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.message.Message;
import org.telegram.telegrambots.meta.generics.TelegramClient;

public interface ICmdHandler {

    public BotApiMethod dealMsg(TelegramClient client, Message message, BtnCallCmd callCmd, String... params) throws Exception;
}
