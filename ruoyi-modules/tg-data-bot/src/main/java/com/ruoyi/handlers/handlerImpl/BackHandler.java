package com.ruoyi.handlers.handlerImpl;

import com.ruoyi.common.core.utils.SpringUtils;
import com.ruoyi.common.core.utils.StringUtils;
import org.springframework.stereotype.Component;
import com.ruoyi.constant.BtnCallCmd;
import com.ruoyi.handlers.IBackHandler;
import com.ruoyi.handlers.ICmdHandler;
import com.ruoyi.handlers.model.MsgData;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.message.Message;
import org.telegram.telegrambots.meta.generics.TelegramClient;

@Component
public class BackHandler implements ICmdHandler {

    @Override
    public EditMessageText dealMsg(TelegramClient client, Message message, BtnCallCmd callCmd, String... params) {
        String handlerClassName = params[0];
        try {
             IBackHandler obj = (IBackHandler) SpringUtils.getBean(StringUtils.lowerFirst(handlerClassName));
            MsgData msgData = obj.getMsgData(message, params);

            EditMessageText msg = new EditMessageText(msgData.getText());
            msg.setChatId(message.getChatId());
            msg.setMessageId(message.getMessageId());
            msg.setReplyMarkup(msgData.getReplyMarkup());
            msg.setParseMode(ParseMode.MARKDOWN);
            return msg;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
