package com.ruoyi.handlers.handlerImpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.ruoyi.constant.BtnCallCmd;
import com.ruoyi.handlers.CmdUtil;
import com.ruoyi.handlers.ICmdHandler;
import org.telegram.telegrambots.meta.api.methods.botapimethods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.objects.message.Message;
import org.telegram.telegrambots.meta.generics.TelegramClient;

@Component
public class DelMsgHandler implements ICmdHandler {

    @Autowired
    private CmdUtil cmdUtil;

    @Override
    public BotApiMethod dealMsg(TelegramClient client, Message message, BtnCallCmd callCmd, String... params) {
        String orderId = params[0];

        BotApiMethod msg = new DeleteMessage(message.getChat().getId().toString(), message.getMessageId());
        cmdUtil.clearCmd(message.getChatId().toString());
        return msg;
    }

}
