package com.ruoyi.handlers.handlerImpl;

import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.util.RandomUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.ruoyi.common.core.utils.DateUtils;
import com.ruoyi.common.core.utils.StringUtils;
import com.ruoyi.common.redis.constant.DefaultJedisKeyNS;
import com.ruoyi.common.redis.service.RedisService;
import com.ruoyi.config.BotClientConfig;
import com.ruoyi.constant.BtnCallCmd;
import com.ruoyi.db.domain.AgentBindInfo;
import com.ruoyi.db.domain.DataInfo;
import com.ruoyi.db.domain.DomainInfo;
import com.ruoyi.db.service.IAgentBindInfoService;
import com.ruoyi.db.service.IDataInfoService;
import com.ruoyi.db.service.IDomainInfoService;
import com.ruoyi.handlers.CmdUtil;
import com.ruoyi.handlers.IBackHandler;
import com.ruoyi.handlers.ICmdHandler;
import com.ruoyi.handlers.model.ICmdInputHandler;
import com.ruoyi.handlers.model.MsgData;
import com.ruoyi.model.DataInfoScore;
import com.ruoyi.model.DataScore;
import com.ruoyi.utils.ThreadUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.botapimethods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.message.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.util.*;
import java.util.stream.Collector;
import java.util.stream.Collectors;

@Component
@Slf4j
public class DataInfoHandler implements ICmdHandler, IBackHandler, ICmdInputHandler {

    @Autowired
    private CmdUtil cmdUtil;

    @Autowired
    private IAgentBindInfoService agentBindInfoService;

    @Autowired
    private IDataInfoService dataInfoService;

    @Autowired
    private IDomainInfoService domainInfoService;

    @Autowired
    private BotClientConfig botClientConfig;

    @Autowired
    private RedisService redisService;

    @Override
    public BotApiMethod dealMsg(TelegramClient client, Message message, BtnCallCmd cmd, String... params) {

        BotApiMethod methodMsg = null;
        if(BtnCallCmd.QueryData.equals(cmd)) {
            MsgData msgData = getQueryDataList(message,params);
            SendMessage msg = new SendMessage(message.getChat().getId().toString(), msgData.getText());
            msg.setReplyMarkup(msgData.getReplyMarkup());
            msg.setParseMode(ParseMode.MARKDOWN);
            methodMsg = msg;
        }else if(BtnCallCmd.QueryToday.equals(cmd)) {
            String bindId = params.length > 0?params[0]: null;
            String date = DateUtils.getDate();
            String agentCode = null;
            String domain = null;
            if(bindId != null) {
                AgentBindInfo agent = agentBindInfoService.getById(bindId);
                agentCode = agent.getAgentCode();
                domain = agent.getDomain();
            }
            MsgData msgData = getData(message,agentCode, domain, date);
            String text = ("\uD83D\uDECE "+date+" 查询结果\n➖➖➖➖➖➖➖➖➖➖\n") + msgData.getText();
            SendMessage msg = new SendMessage(message.getChat().getId().toString(), text);
            msg.setReplyMarkup(msgData.getReplyMarkup());
            msg.setParseMode(ParseMode.MARKDOWN);
            methodMsg = msg;
        }else if(BtnCallCmd.QueryYesterday.equals(cmd)) {
            String bindId = params[0];
            String date = DateUtils.getYesterDay();

            String agentCode = null;
            String domain = null;
            if(bindId != null) {
                AgentBindInfo agent = agentBindInfoService.getById(bindId);
                agentCode = agent.getAgentCode();
                domain = agent.getDomain();
            }
            MsgData msgData = getData(message,agentCode, domain, date);
            String text = ("\uD83D\uDECE "+date+" 查询结果\n➖➖➖➖➖➖➖➖➖➖\n") + msgData.getText();
            SendMessage msg = new SendMessage(message.getChat().getId().toString(), text);
            msg.setReplyMarkup(msgData.getReplyMarkup());
            msg.setParseMode(ParseMode.MARKDOWN);
            methodMsg = msg;
        }else if(BtnCallCmd.QueryTextData.equals(cmd)) {
            String agentCode = params[1];
            String domain = null;
            String date = params[0];

            MsgData msgData = getData(message,agentCode, domain, date);
            String text = ("\uD83D\uDECE "+(agentCode != null?("`"+agentCode+"`"):"")+" 查询结果\n➖➖➖➖➖➖➖➖➖➖\n") + msgData.getText();
            SendMessage msg = new SendMessage(message.getChat().getId().toString(), text);
            msg.setReplyMarkup(msgData.getReplyMarkup());
            msg.setParseMode(ParseMode.MARKDOWN);
            methodMsg = msg;
        }else if(BtnCallCmd.QueryCustom.equals(cmd)) {
            String bindId = params.length > 0?params[0]:null;

            String text = "";
            if(StringUtils.isBlank(bindId)) {
                text = "请输入查询代理及日期， 格式如下 \n `abcd1234 " + DateUtils.getDate() + "`\n";
            }else {
                text = "请输入查询日期， 格式如下 \n `" + DateUtils.getDate() + "`\n";
            }
            SendMessage msg = new SendMessage(message.getChat().getId().toString(), text);
            msg.setParseMode(ParseMode.MARKDOWN);
            methodMsg = msg;

            cmdUtil.putCmd(cmd, message.getChat().getId().toString(), bindId);
        }else {
            MsgData msgData = getMsgData(message, params);
            SendMessage msg = new SendMessage(message.getChat().getId().toString(), msgData.getText());
            msg.setReplyMarkup(msgData.getReplyMarkup());
            msg.setParseMode(ParseMode.MARKDOWN);
            methodMsg = msg;
        }
        return methodMsg;
    }

    private MsgData buildDataMsg(String agentCode, String domain, String date, List<DataInfo> dataInfos) {
        if(dataInfos.size() == 1) {
            domain = dataInfos.get(0).getDomain();
        }
        boolean onlyOne = StringUtils.isNotBlank(domain);
        String msgText = "";
        msgText += "代理: `"+ (agentCode == null?"无":agentCode)+ "`  " + (onlyOne?("（`"+ domain +"`）"):"")+ "\n";
        if(CollectionUtils.isNotEmpty(dataInfos)) {
            for(DataInfo dataInfo : dataInfos) {
                msgText += (!onlyOne?("`"+ dataInfo.getDomain() +"`"):"") + "`注册人数: "+ dataInfo.getRegisterNum() +", 转化人数: "+ dataInfo.getEffectiveNum() +"（"+ dataInfo.getConvertRate() +"%）, 续存人数: "+ dataInfo.getRepeatNum() + "（"+ dataInfo.getRenewRate() +"%）` \n";

//                DataInfoScore dataInfoScore = buildScore(dataInfo);
//
//                if(dataInfoScore != null && dataInfoScore.getConvert() != null) {
//
//                }
//                if(dataInfoScore != null && dataInfoScore.getRenew() != null) {
//
//                }
            }
            msgText += "➖➖➖➖➖➖➖➖➖➖\n";
        }else {
            msgText += "➖➖➖➖➖➖➖➖➖➖\n";
            msgText += "\n暂无统计数据\n";
        }
        MsgData msgData = new MsgData();
        msgData.setText(msgText);

        return msgData;
    }

    private DataInfoScore buildScore(DataInfo dataInfo) {
        List<DataScore> configs = redisService.getCacheList(DefaultJedisKeyNS.global_config, "convert_config_"+dataInfo.getBotId());
        if(configs == null) {
            configs = dataInfoService.getConvertConfig(dataInfo.getBotId());

            if(CollectionUtils.isEmpty(configs)) {
                configs = new ArrayList<>();
            }
            redisService.setCacheList(DefaultJedisKeyNS.global_config, "convert_config_"+dataInfo.getBotId(), configs);
        }
        DataScore convertScore = null;
        if(CollectionUtils.isNotEmpty(configs)) {
            configs.sort(new Comparator<DataScore>() {
                @Override
                public int compare(DataScore o1, DataScore o2) {
                    return o1.getPriority() - o2.getPriority();
                }
            });
            for(DataScore config : configs) {
                if(dataInfo.getConvertRate() >= config.getScore()) {
                    convertScore = config;
                    break;
                }
            }
            convertScore = convertScore == null?configs.get(configs.size()-1):convertScore;
        }
        configs = redisService.getCacheList(DefaultJedisKeyNS.global_config, "renew_config_"+dataInfo.getBotId());
        if(configs == null) {
            configs = dataInfoService.getRenewConfig(dataInfo.getBotId());

            if(CollectionUtils.isEmpty(configs)) {
                configs = new ArrayList<>();
            }
            redisService.setCacheList(DefaultJedisKeyNS.global_config, "renew_config_"+dataInfo.getBotId(), configs);
        }
        DataScore renewScore = null;
        if(CollectionUtils.isNotEmpty(configs)) {
            configs.sort(new Comparator<DataScore>() {
                @Override
                public int compare(DataScore o1, DataScore o2) {
                    return o1.getPriority() - o2.getPriority();
                }
            });
            for(DataScore config : configs) {
                if(dataInfo.getRenewRate() >= config.getScore()) {
                    renewScore = config;
                    break;
                }
            }
            renewScore = renewScore == null?configs.get(configs.size()-1):renewScore;
        }
        return new DataInfoScore(convertScore, renewScore);
    }

    private MsgData getData(Message message, String agentCode, String domain, String date) {

        List<DataInfo> dataInfos = new ArrayList<>();

        boolean needNew = date.equals(DateUtils.getDate());
        if(needNew) {
            DomainInfo queryDomain = new DomainInfo();
            queryDomain.setDomain(domain);
            String botId = ThreadUtil.getBotId();
            queryDomain.setBotId(botId);
            QueryWrapper<DomainInfo> baseWrapper = domainInfoService.getBaseWrapper(queryDomain);
            String sql = String.format("select domain from agent_bind_info where user_id = '%s' and bot_id = '%s'", message.getChatId(), botId);
            if(StringUtils.isNotBlank(agentCode)) {
                sql += String.format("and agent_code = '%s'", agentCode);
            }
            baseWrapper.lambda().inSql(DomainInfo::getDomain, sql);
            List<DomainInfo> domainInfos = domainInfoService.list(baseWrapper);

            String text = "";
            if(CollectionUtils.isNotEmpty(domainInfos)) {
                AgentBindInfo queryData = new AgentBindInfo();
                queryData.setUserId(message.getChatId());
                queryData.setBotId(botId);
                List<AgentBindInfo> agentBindInfos = agentBindInfoService.selectList(queryData);

                Map<String, List<String>> domainAgents = agentBindInfos.stream()
                        .collect(Collectors.groupingBy(
                                AgentBindInfo::getDomain,
                                Collectors.mapping(AgentBindInfo::getAgentCode, Collectors.toList())
                        ));

                for(DomainInfo domainInfo : domainInfos) {
                    List<String> agentCodes = domainAgents.get(domainInfo.getDomain());
                    dataInfos = dataInfoService.loadRemoteData(domainInfo, agentCode, date);
                    Iterator<DataInfo> iterator = dataInfos.iterator();
                    while(iterator.hasNext()) {
                        DataInfo next = iterator.next();
                        if (!agentCodes.contains(next.getAgentCode())) {//剔除 非本群代理
                            iterator.remove();
                        }else {
                            next.setDomain(domainInfo.getDomain());
                        }
                    }
                    if(CollectionUtils.isNotEmpty(dataInfos)) {
                        Map<String/*agentCode*/, List<DataInfo>> collect = dataInfos.stream().collect(Collectors.groupingBy(DataInfo::getAgentCode));

                        for(String agent : collect.keySet()) {
                            List<DataInfo> subDatas = collect.get(agent);
                            MsgData msgData = buildDataMsg(agent, null, date, subDatas);

                            text += msgData.getText() + "\n";
                        }
                    }else {
                        text += "代理: `"+ (agentCode == null?"无":agentCode)+ "`  " +("（`"+ domainInfo.getDomain() +"`）") + "\n";
                        text += "➖➖➖➖➖➖➖➖➖➖\n";
                    }

                }
            }
            MsgData msgData = new MsgData();
            msgData.setText(text);
            return msgData;
        }else {
            String botId = ThreadUtil.getBotId();

            DataInfo queryData = new DataInfo();
            queryData.setAgentCode(agentCode);
            queryData.setDataDate(date);
            queryData.setDomain(domain);
            queryData.setBotId(botId);
            QueryWrapper<DataInfo> baseWrapper = dataInfoService.getBaseWrapper(queryData);

            String sql1 = String.format("select domain from agent_bind_info where user_id = '%s' and bot_id = '%s'", message.getChatId(), botId);
            String sql2 = String.format("select agent_code from agent_bind_info where user_id = '%s' and bot_id = '%s'", message.getChatId(), botId);
            baseWrapper.lambda()
                    .inSql(DataInfo::getDomain, sql1)
                    .inSql(DataInfo::getAgentCode, sql2);

            dataInfos = dataInfoService.list(baseWrapper);

            Map<String/*agentCode*/, List<DataInfo>> collect = dataInfos.stream().collect(Collectors.groupingBy(DataInfo::getAgentCode));

            String text = "";
            for(String agent : collect.keySet()) {
                List<DataInfo> subDatas = collect.get(agent);
                MsgData msgData = buildDataMsg(agent, null, date, subDatas);

                text += msgData.getText() + "\n";
            }
            MsgData msgData = new MsgData();
            msgData.setText(text);
            return msgData;
        }
    }

    private MsgData getQueryDataList(Message message, String[] params) {
        MsgData msgData = new MsgData();

        String msgText = "";
        List<InlineKeyboardRow> rows = new ArrayList<>();

        InlineKeyboardRow btnRow = null;
        InlineKeyboardButton btn = null;
        BtnCallCmd cmd = null;

        String bindId = params[0];
        AgentBindInfo agent = agentBindInfoService.getById(Long.parseLong(bindId));

        msgText += "\uD83D\uDECE 当前代理: `"+ agent.getAgentCode()+ "` 请选择时间进行查询\n";
        msgText += "➖➖➖➖➖➖➖➖➖➖\n";
        btnRow = new InlineKeyboardRow();

        cmd = BtnCallCmd.QueryToday;
        btn = new InlineKeyboardButton(cmd.getText());
        btn.setCallbackData(cmd.getCallData(bindId));
        btnRow.add(btn);

        cmd = BtnCallCmd.QueryYesterday;
        btn = new InlineKeyboardButton(cmd.getText());
        btn.setCallbackData(cmd.getCallData(bindId));
        btnRow.add(btn);

        cmd = BtnCallCmd.QueryCustom;
        btn = new InlineKeyboardButton(cmd.getText());
        btn.setCallbackData(cmd.getCallData(bindId));
        btnRow.add(btn);

        rows.add(btnRow);

        btnRow = new InlineKeyboardRow();
        cmd = BtnCallCmd.BackCmd;
        btn = new InlineKeyboardButton(cmd.getText());
        btn.setCallbackData(cmd.getCallData("DataInfoHandler"));
        btnRow.add(btn);
        rows.add(btnRow);

        InlineKeyboardMarkup markup = new InlineKeyboardMarkup(rows);
        msgData.setText(msgText);
        msgData.setReplyMarkup(markup);
        return msgData;
    }

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

        msgText += "\uD83D\uDECE请选择代理列表进行查询 ("+ agentBindInfos.size() +") \n";
        msgText += "➖➖➖➖➖➖➖➖➖➖\n";
        for(AgentBindInfo data : agentBindInfos) {
            btnRow = new InlineKeyboardRow();
            cmd = BtnCallCmd.QueryData;

            btn = new InlineKeyboardButton(data.getAgentCode() + "（"+ data.getDomain() +"）" );
            btn.setCallbackData(cmd.getCallData(data.getBindId()+""));
            btnRow.add(btn);
            rows.add(btnRow);
        }

        InlineKeyboardMarkup markup = new InlineKeyboardMarkup(rows);
        msgData.setText(msgText);
        msgData.setReplyMarkup(markup);
        return msgData;
    }

    @Override
    public void doBusiness(TelegramClient client, Message message, BtnCallCmd cmd, String param) {
        String[] p = message.getText().trim().replaceAll("\\s+", " ").split(" ");
        String dateStr = null;
        String agentCode = null;
        if(p.length == 1 && DateUtils.parseDate(p[0]) != null) {
            dateStr = p[0];
        }else if(p.length == 2){
            if(DateUtils.parseDate(p[0]) != null) {//2025-08-08 abcd1234
                dateStr = p[0];
                agentCode = p[1];
            }else if( DateUtils.parseDate(p[1]) != null) {//abcd1234 2025-08-08
                dateStr = p[1];
                agentCode = p[0];
            }
        }
        String bindId = param;

        if(dateStr != null) {
            String domain = null;
            if(bindId != null) {
                AgentBindInfo agent = agentBindInfoService.getById(bindId);
                agentCode = agent.getAgentCode();
                domain = agent.getDomain();
            }
            MsgData msgData = getData(message,agentCode, domain, dateStr);

            String text = ("\uD83D\uDECE "+dateStr+" 查询结果\n➖➖➖➖➖➖➖➖➖➖\n") + msgData.getText();
            SendMessage msg = new SendMessage(message.getChatId().toString(), text);
            msg.setReplyMarkup(msgData.getReplyMarkup());
            msg.setParseMode(ParseMode.MARKDOWN);
            try {
                client.execute(msg);
            } catch (TelegramApiException e) {
                log.error("Error processing non-command send", e);
            }
            cmdUtil.clearCmd(message.getChatId().toString());
        }else{
            SendMessage msg = new SendMessage(message.getChat().getId().toString(), "时间格式不正确");
            try {
                client.execute(msg);
            } catch (TelegramApiException e2) {
                log.error("err date format warn", e2);
            }
        }
    }

    public void sendStatMsg(Long userId, String date, List<DataInfo> dataInfos) {

        Map<String/*agentCode*/, List<DataInfo>> collect = dataInfos.stream().collect(Collectors.groupingBy(DataInfo::getAgentCode));

        String text = ("\uD83D\uDECE "+date+" 投放数据\n");

        for(String agent : collect.keySet()) {
            List<DataInfo> subDatas = collect.get(agent);
            MsgData msgData = buildDataMsg(agent, null, date, subDatas);

            text += msgData.getText() + "\n";
        }

        SendMessage msg = new SendMessage(userId.toString(), text);
        try {
            msg.setParseMode(ParseMode.MARKDOWN);

            String botId = dataInfos.get(0).getBotId();
            TelegramClient client = botClientConfig.getClient(botId);
            client.execute(msg);
        } catch (TelegramApiException e2) {
            log.error("err send stat date warn", e2);
        }

    }

    public static void main(String[] args) {
        System.out.println(RandomUtil.randomString(8));
    }
}
