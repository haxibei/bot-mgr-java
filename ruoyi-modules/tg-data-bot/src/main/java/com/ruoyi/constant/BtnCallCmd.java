package com.ruoyi.constant;

import com.ruoyi.common.core.utils.SpringUtils;
import com.ruoyi.handlers.handlerImpl.AgentHandler;
import com.ruoyi.handlers.handlerImpl.DataInfoHandler;
import org.apache.commons.lang3.StringUtils;
import com.ruoyi.handlers.ICmdHandler;
import com.ruoyi.handlers.handlerImpl.BackHandler;
import com.ruoyi.handlers.handlerImpl.DelMsgHandler;
import com.ruoyi.handlers.model.BtnCmdCallData;
import com.ruoyi.handlers.model.ICmdInputHandler;


public enum BtnCallCmd {
    None("占位", "", null),
    AgentList("代理列表", "%s", AgentHandler.class),
    AddAgent("添加代理", "%s", AgentHandler.class),
    DelAgentList("删除代理列表", "%s", AgentHandler.class),
    DoDelAgent("❌", "%s", AgentHandler.class),

    DelMsg("删除消息", "", DelMsgHandler.class),


    QueryToday("\uD83D\uDCD5今日", "%s", DataInfoHandler.class),
    QueryYesterday("昨日", "%s", DataInfoHandler.class),
    QueryCustom("\uD83D\uDEAB自定义查询", "%s", DataInfoHandler.class),
    QueryData("查询", "%s", DataInfoHandler.class),
    QueryTextData("文本查询", "%s", DataInfoHandler.class),
    BackCmd("«返回", "%s", BackHandler.class),

    ;

    private String text;

    private String paramJson;

    private Class<?> handler;

    private BtnCallCmd(String text, String paramJson, Class<?> handler) {
        this.text = text;
        this.paramJson = paramJson;
        this.handler = handler;
    }

    public static BtnCallCmd getByName(String name) {
        BtnCallCmd[] values = BtnCallCmd.values();
        for(BtnCallCmd cmd : values) {
            if(cmd.name().equals(name)) {
                return cmd;
            }
        }
        return null;
    }

    public String getText(){
        return text;
    }

    public ICmdHandler getHandler(){
        return handler == null?null:(ICmdHandler) SpringUtils.getBean(handler);
    }

    public ICmdInputHandler getInputHandler() {
        return handler == null?null:(ICmdInputHandler) SpringUtils.getBean(handler);
    }

    public String getCallData(String param) {
        //TODO 这里后续可以用redis 缓存存储短码 ，在回收消息的时候进行解码， 短码有效时间尽可能在1天内有效
        String p = String.format(this.paramJson, param);
        return this.name() + (StringUtils.isNotBlank(p)?("|"+ p):"");
    }

    public String getCallData() {
        return getCallData(null) ;
    }

    public static BtnCmdCallData parseCallData(String data) {
        String[] arr = data.split("\\|");
        return new BtnCmdCallData(arr[0], arr.length > 1?arr[1]: null);
    }


}
