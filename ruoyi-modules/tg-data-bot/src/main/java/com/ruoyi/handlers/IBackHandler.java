package com.ruoyi.handlers;

import com.ruoyi.handlers.model.MsgData;
import org.telegram.telegrambots.meta.api.objects.message.Message;

public interface IBackHandler {

    public MsgData getMsgData(Message message, String... params);
}
