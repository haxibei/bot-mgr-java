package com.ruoyi.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
public class TgMsgContent {

    private String chatId; //目标会话

    private String msg; //消息内容

    private int type;// 0  发送消息   1  转发消息

    private String fromChatId;//转发消息的来源

    private String tgMsgId; //转发的消息id

    private String imgList;

    private String videoList;
}
