package com.ruoyi.commands;

import com.ruoyi.common.core.utils.DateUtils;
import com.ruoyi.common.core.utils.RegexUtil;
import com.ruoyi.common.core.utils.SpringUtils;
import com.ruoyi.config.properties.BotConfig;
import com.ruoyi.db.domain.QueryBlack;
import com.ruoyi.db.domain.QueryLog;
import com.ruoyi.db.service.IQueryBlackService;
import com.ruoyi.db.service.IQueryLogService;
import com.ruoyi.handlers.handlerImpl.DataInfoHandler;
import com.ruoyi.handlers.model.MsgData;
import com.ruoyi.utils.ThreadUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.telegram.telegrambots.extensions.bots.commandbot.commands.BotCommand;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.api.objects.chat.Chat;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

/**
 * This commands starts the conversation with the bot
 *
 * @author Timo Schulz (Mit0x2)
 */
@Slf4j
public class CustomCommand extends BotCommand {

    private BotConfig botConfig;

    private final String commandIdentifier;

    private final String domain;

    public CustomCommand(String commandIdentifier, String description, String domain, BotConfig botConfig) {
        super(commandIdentifier, description);
        this.botConfig = botConfig;
        this.commandIdentifier = commandIdentifier;
        this.domain = domain;
    }

    @Override
    public void execute(TelegramClient telegramClient, User user, Chat chat, String[] params) {
        ThreadUtil.clear();
        String botId = botConfig.getBotId();
        ThreadUtil.setBotId(botId);

        IQueryBlackService queryBlackService = SpringUtils.getBean(IQueryBlackService.class);
        QueryBlack queryBlack = new QueryBlack();
        queryBlack.setTgId(user.getId().toString());
//        queryLog.setBotId(botId);
        long count = queryBlackService.count(queryBlackService.getBaseWrapper(queryBlack));
        if(count > 0) {
            SendMessage answer = new SendMessage(chat.getId().toString(), "暂时没有查询权限");
            answer.setParseMode(ParseMode.MARKDOWN);
            try {
                telegramClient.execute(answer);
            } catch (TelegramApiException e) {
                log.error("自定义命令处理消息失败1", e);
            }
            return;
        }

        StringBuffer messageBuilder = new StringBuffer();
        if(params.length < 1) {
            messageBuilder.append("参数错误，完整命令如下\n");
            messageBuilder.append("/").append(commandIdentifier).append(" dx2052\n");
            messageBuilder.append("/").append(commandIdentifier).append(" dx2052 2026-02\n");
            messageBuilder.append("/").append(commandIdentifier).append(" dx2052 2026-02-18");
        }else {
            String agent = params[0];
            String date;
            if(params.length > 1) {
                date = params[1];
            }else {
                date = DateUtils.getDate();
            }
            if (!RegexUtil.isDate(date) && !DateUtils.isMonth(date)) {
                messageBuilder.append("日期格式错误，完整命令如下\n");
                messageBuilder.append("/").append(commandIdentifier).append(" dx2052 2026-02\n");
                messageBuilder.append("/").append(commandIdentifier).append(" dx2052 2026-02-18");
            }else {
                DataInfoHandler dataInfoHandler = SpringUtils.getBean(DataInfoHandler.class);
                MsgData data = dataInfoHandler.getDataLv1(chat.getId(), agent, domain, date);

                //记录查询日志
                recordQuery(user, domain, agent, date, data.getText());
                messageBuilder.append(data.getText());
            }
        }
        SendMessage answer = new SendMessage(chat.getId().toString(), messageBuilder.toString());
        answer.setParseMode(ParseMode.MARKDOWN);
        try {
            telegramClient.execute(answer);
        } catch (TelegramApiException e) {
            log.error("自定义命令处理消息失败", e);
        }
    }

    private void recordQuery(User user, String domain, String agent, String date, String text) {
        String botId = ThreadUtil.getBotId();
        IQueryLogService queryLogService = SpringUtils.getBean(IQueryLogService.class);

        QueryLog queryLog = new QueryLog();
        queryLog.setTgId(user.getId().toString());
        String name = (StringUtils.isNotBlank(user.getFirstName())?user.getFirstName():"")  + (StringUtils.isNotBlank(user.getLastName())?user.getLastName():"");
        queryLog.setTgName(name);
        queryLog.setTgUser(user.getUserName());
        queryLog.setDomain(this.getCommandIdentifier());
        queryLog.setAgent(agent);
        queryLog.setDate(date);
        queryLog.setQueryResult(text);
        queryLog.setBotId(botId);
        queryLogService.insertOrUpdate(queryLog);
    }

}
