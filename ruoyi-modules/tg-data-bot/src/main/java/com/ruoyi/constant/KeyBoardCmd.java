package com.ruoyi.constant;

import com.ruoyi.common.core.utils.SpringUtils;
import com.ruoyi.handlers.handlerImpl.AgentHandler;
import com.ruoyi.handlers.ICmdHandler;
import com.ruoyi.handlers.handlerImpl.DataInfoHandler;
import org.telegram.telegrambots.meta.api.methods.botapimethods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.message.Message;

public enum KeyBoardCmd {

    AgentList("\uD83D\uDC65代理查询", AgentHandler.class, null),
    AgentAdd("➕添加代理", AgentHandler.class, BtnCallCmd.AddAgent),
    AgentDel("⚠\uFE0F删除代理", AgentHandler.class, BtnCallCmd.DoDelAgent),
    QueryData("\uD83D\uDD79数据查询", DataInfoHandler.class, null),
    QueryToday("\uD83D\uDD0D查询当天", DataInfoHandler.class, BtnCallCmd.QueryToday),
    QueryCustom("\uD83D\uDCCA查询历史", DataInfoHandler.class, BtnCallCmd.QueryCustom),
    ;

    private String text;

    private Class<?> handler;

    private BtnCallCmd btnCmd;

    private KeyBoardCmd(String text, Class<?> handler, BtnCallCmd btnCmd) {
        this.text = text;
        this.handler = handler;
        this.btnCmd = btnCmd;
    }

    public static KeyBoardCmd getByText(String text) {
        KeyBoardCmd[] values = KeyBoardCmd.values();
        for(KeyBoardCmd cmd : values) {
            if(cmd.text.equals(text)) {
                return cmd;
            }
        }
        return null;
    }

    public String getText(){
        return text;
    }

    public BtnCallCmd getBtnCmd(){
        return btnCmd;
    }

    public BotApiMethod getMsg(Message message, BtnCallCmd cmd) throws Exception{
        ICmdHandler cmdHandler = (ICmdHandler) SpringUtils.getBean(handler);
        return cmdHandler.dealMsg(null, message, cmd);
    }
}
