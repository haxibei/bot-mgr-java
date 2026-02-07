package com.ruoyi.handlers.model;

import lombok.Data;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;

@Data
public class MsgData {

    private String text;

    private InlineKeyboardMarkup replyMarkup;
}
