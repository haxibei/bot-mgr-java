package com.ruoyi.handlers.handlerImpl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.ruoyi.db.domain.AgentBindInfo;
import com.ruoyi.db.domain.DomainInfo;
import com.ruoyi.db.service.IAgentBindInfoService;
import com.ruoyi.db.service.IDomainInfoService;
import com.ruoyi.handlers.CmdUtil;
import com.ruoyi.handlers.model.ICmdInputHandler;
import com.ruoyi.utils.ThreadUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.ruoyi.constant.BtnCallCmd;
import com.ruoyi.handlers.IBackHandler;
import com.ruoyi.handlers.ICmdHandler;
import com.ruoyi.handlers.model.MsgData;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.botapimethods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.message.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;
import java.util.ArrayList;
import java.util.List;

@Component
@Slf4j
public class AgentHandler implements ICmdHandler, IBackHandler, ICmdInputHandler {

    @Autowired
    private CmdUtil cmdUtil;

    @Autowired
    private IAgentBindInfoService agentBindInfoService;

    @Autowired
    private IDomainInfoService domainInfoService;

    @Override
    public BotApiMethod dealMsg(TelegramClient client, Message message, BtnCallCmd cmd, String... params) {
        BotApiMethod methodMsg = null;
        if(BtnCallCmd.AddAgent.equals(cmd)) {
            String text = "请输入添加域名及代理:\n" +
                    "域名和代理请用空格分开，格式如下 \n" +
                    "`www.domain.com abcd1234`\n" +
                    "`www.domain.com abcd1234`\n" +
                    "`www.domain.com abcd1234`\n";

            SendMessage msg = new SendMessage(message.getChat().getId().toString(), text);
            msg.setParseMode(ParseMode.MARKDOWN);

            methodMsg = msg;

            cmdUtil.putCmd(cmd, message.getChat().getId().toString());
        }else if(BtnCallCmd.DelAgentList.equals(cmd)) {
            MsgData msgData = getDelListMsg(message,params);

            EditMessageText editMessage = new EditMessageText(msgData.getText());
            editMessage.setChatId(message.getChatId());
            editMessage.setMessageId(message.getMessageId());
            editMessage.setReplyMarkup(msgData.getReplyMarkup());
            editMessage.setParseMode(ParseMode.MARKDOWN);
            methodMsg = editMessage;
        }else if(BtnCallCmd.DoDelAgent.equals(cmd)) {
            if(params.length < 1 || params[0] == null || params[0].trim().equals("")) {
                String text = "请输入删除的域名及代理:\n" +
                        "域名和代理请用空格分开，格式如下 \n" +
                        "`www.domain.com abcd1234`\n" +
                        "`www.domain.com abcd1234`\n" +
                        "`www.domain.com abcd1234`\n";
                SendMessage msg = new SendMessage(message.getChat().getId().toString(), text);
                msg.setParseMode(ParseMode.MARKDOWN);
                methodMsg = msg;
                cmdUtil.putCmd(cmd, message.getChat().getId().toString());
            }else {
                // 删除
                agentBindInfoService.removeById(Long.parseLong(params[0]));

                MsgData msgData = getMsgData(message,params);
                EditMessageText editMessage = new EditMessageText(msgData.getText());
                editMessage.setChatId(message.getChatId());
                editMessage.setMessageId(message.getMessageId());
                editMessage.setReplyMarkup(msgData.getReplyMarkup());
                editMessage.setParseMode(ParseMode.MARKDOWN);
                methodMsg = editMessage;
            }
        }else {
            MsgData msgData = getMsgData(message, params);

            SendMessage msg = new SendMessage(message.getChat().getId().toString(), msgData.getText());
            msg.setParseMode(ParseMode.MARKDOWN);
            msg.setReplyMarkup(msgData.getReplyMarkup());
            methodMsg = msg;
        }
        return methodMsg;
    }

    public MsgData getDelListMsg(Message message, String... params) {
        MsgData msgData = new MsgData();

        AgentBindInfo queryData = new AgentBindInfo();
        queryData.setUserId(message.getChatId());
        queryData.setBotId(ThreadUtil.getBotId());
        List<AgentBindInfo> agentBindInfos = agentBindInfoService.selectList(queryData);

        Integer num = agentBindInfos.size();

        //展示列表 并更新 原消息
        String msgText ="\uD83D\uDECE代理列表 ("+num+")\n" +
                "➖➖➖➖➖➖➖➖➖➖\n" +
                "\uD83D\uDC47点击下方地址按钮删除代理";

        List<InlineKeyboardRow> rows = new ArrayList<>();

        InlineKeyboardRow btnRow = new InlineKeyboardRow();
        BtnCallCmd cmd = null;
        InlineKeyboardButton btn = null;

        for(AgentBindInfo data : agentBindInfos) {
            btnRow = new InlineKeyboardRow();
            cmd = BtnCallCmd.DoDelAgent;

            btn = new InlineKeyboardButton(cmd.getText() + data.getAgentCode() + "（"+ data.getDomain() +"）" );

            btn.setCallbackData(cmd.getCallData(data.getBindId()+""));
            btnRow.add(btn);
            rows.add(btnRow);
        }

        btnRow = new InlineKeyboardRow();
        cmd = BtnCallCmd.BackCmd;
        btn = new InlineKeyboardButton(cmd.getText());
        btn.setCallbackData(cmd.getCallData("AgentHandler"));
        btnRow.add(btn);

        rows.add(btnRow);

        InlineKeyboardMarkup markup = new InlineKeyboardMarkup(rows);

        msgData.setText(msgText);
        msgData.setReplyMarkup(markup);
        return msgData;
    }

    @Override
    public MsgData getMsgData(Message message, String... params) {
        MsgData msgData = new MsgData();

        String msgText = "";
        List<InlineKeyboardRow> rows = new ArrayList<>();

        InlineKeyboardRow btnRow = null;
        InlineKeyboardButton btn = null;
        BtnCallCmd cmd = null;

        //代理列表
        AgentBindInfo queryData = new AgentBindInfo();
        queryData.setUserId(message.getChatId());
        queryData.setBotId(ThreadUtil.getBotId());
        List<AgentBindInfo> agentBindInfos = agentBindInfoService.selectList(queryData);

        msgText += "\uD83D\uDECE代理列表 ("+ agentBindInfos.size() +") \n";
        msgText += "➖➖➖➖➖➖➖➖➖➖\n";
        msgText += "每日转点后20分钟内自动推送消息\n\n";
        for(AgentBindInfo data : agentBindInfos) {
            msgText += "`" + data.getDomain() + " " + data.getAgentCode() + "` \n";
        }
        btnRow = new InlineKeyboardRow();
        cmd = BtnCallCmd.AddAgent;
        btn = new InlineKeyboardButton(cmd.getText());
        btn.setCallbackData(cmd.getCallData());
        btnRow.add(btn);

        cmd = BtnCallCmd.DelAgentList;
        btn = new InlineKeyboardButton(cmd.getText());
        btn.setCallbackData(cmd.getCallData());
        btnRow.add(btn);

        rows.add(btnRow);

        InlineKeyboardMarkup markup = new InlineKeyboardMarkup(rows);
        msgData.setText(msgText);
        msgData.setReplyMarkup(markup);
        return msgData;
    }

    @Override
    public void doBusiness(TelegramClient client, Message message, BtnCallCmd callCmd, String param) {

        boolean refresh = false;
        if(BtnCallCmd.AddAgent.equals(callCmd)) {
            refresh = addAgent(client, message);
        }else if(BtnCallCmd.DoDelAgent.equals(callCmd)) {
            refresh = delAgent(client, message);
        }
        if(refresh) {
            cmdUtil.clearCmd(message.getChatId().toString());
            MsgData msgData = getMsgData(message, null);
            SendMessage msg = new SendMessage(message.getChatId().toString(), msgData.getText());
            msg.setReplyMarkup(msgData.getReplyMarkup());
            msg.setParseMode(ParseMode.MARKDOWN);
            try {
                client.execute(msg);
            } catch (TelegramApiException e) {
                log.error("Error processing non-command send", e);
            }
        }
    }

    private boolean delAgent(TelegramClient client, Message message) {
        List<AgentBindInfo> agentBindInfos = parseBindData(client, message);

        if(CollectionUtils.isNotEmpty(agentBindInfos)) {
            String botId = ThreadUtil.getBotId();
            for(AgentBindInfo data : agentBindInfos) {
                agentBindInfoService.delData(data.getUserId(), data.getDomain(), data.getAgentCode(), botId);
            }
            return true;
        }
        return false;
    }

    private boolean addAgent(TelegramClient client, Message message) {
        List<AgentBindInfo> agentBindInfos = parseBindData(client, message);

        if(CollectionUtils.isNotEmpty(agentBindInfos)) {
            for(AgentBindInfo data : agentBindInfos) {
                data.setBotId(ThreadUtil.getBotId());
                agentBindInfoService.insertOrUpdate(data);
            }
            return true;
        }
        return false;
    }

    private List<AgentBindInfo> parseBindData(TelegramClient client, Message message) {
        String[] lineArr = message.getText().trim().split("\n");

        if(lineArr.length < 1) {
            SendMessage msg = new SendMessage(message.getChat().getId().toString(), "参数格式不正确！");
            try {
                client.execute(msg);
            } catch (TelegramApiException e) {
                log.error("del agent err", e);
            }
            return null;
        }

        List<AgentBindInfo> agentBindInfos = new ArrayList<>();
        int idx = 0;
        for(String line : lineArr) {
            String text = line.replaceAll("\\s+", " ");
            String[] p = text.trim().split(" ");
            if(p.length != 2) {
                SendMessage msg = new SendMessage(message.getChat().getId().toString(), "第" + (idx + 1) + "行参数格式不正确！");
                try {
                    client.execute(msg);
                } catch (TelegramApiException e) {
                    log.error("del agent err", e);
                }
                return null;
            }
            String domain = p[0].replace("https://", "").replace("http://", "").replace("//", "").replace("/", "");

            if(domain.startsWith("www.")) {//含www的域名做 兼容没有www的主域名
                DomainInfo queryData = new DomainInfo();
                queryData.setDomain(domain);
                queryData.setBotId(ThreadUtil.getBotId());
                DomainInfo one = domainInfoService.getOne(domainInfoService.getBaseWrapper(queryData));

                if(one == null) {
                    String realDomain = domain.substring(4);
                    queryData.setDomain(realDomain);
                    queryData.setBotId(ThreadUtil.getBotId());
                    one = domainInfoService.getOne(domainInfoService.getBaseWrapper(queryData));
                    if(one != null) {
                        domain = realDomain;
                    }
                }
            }

            String agentCode = p[1];

            AgentBindInfo data = new AgentBindInfo();
            data.setUserId(message.getChatId());
            data.setDomain(domain);
            data.setAgentCode(agentCode);

            agentBindInfos.add(data);

            idx ++;
        }

        return agentBindInfos;
    }
}
