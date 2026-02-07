package com.ruoyi.handlers.model;

import com.ruoyi.constant.BtnCallCmd;
import org.telegram.telegrambots.meta.api.objects.message.Message;
import org.telegram.telegrambots.meta.generics.TelegramClient;

public interface ICmdInputHandler {

    public void doBusiness(TelegramClient client, Message message, BtnCallCmd callCmd, String param);
}
