package com.ruoyi.commands;

import com.ruoyi.common.core.utils.SpringUtils;
import com.ruoyi.config.properties.BotConfig;
import com.ruoyi.constant.KeyBoardCmd;
import com.ruoyi.db.service.IAgentBindInfoService;
import com.ruoyi.db.service.impl.AgentBindInfoServiceImpl;
import com.ruoyi.handlers.CmdUtil;
import com.ruoyi.services.BotConfigService;
import com.ruoyi.utils.ThreadUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.telegram.telegrambots.extensions.bots.commandbot.commands.BotCommand;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.api.objects.chat.Chat;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.util.ArrayList;
import java.util.List;

/**
 * This commands starts the conversation with the bot
 *
 * @author Timo Schulz (Mit0x2)
 */
@Slf4j
public class StopRecvMsgCommand extends BotCommand {

    private BotConfig botConfig;

    public StopRecvMsgCommand(BotConfig botConfig) {
        super("stop_recv_msg", "With this command you can stop receive group msg from Bot");
        this.botConfig = botConfig;
    }

    @Override
    public void execute(TelegramClient telegramClient, User user, Chat chat, String[] strings) {
        ThreadUtil.clear();
        ThreadUtil.setBotId(botConfig.getBotId());

        StringBuilder messageBuilder = new StringBuilder();
        if(chat.getId() < 0) {//群或频道
            IAgentBindInfoService agentBindInfoService = SpringUtils.getBean(AgentBindInfoServiceImpl.class);
            agentBindInfoService.pauseSendMsg(chat.getId());

            messageBuilder.append("收到暂停指令，本群的定时广告数据推送已即刻停止。");
        }else {
            messageBuilder.append("请在接收消息的群组内试用该命令");
        }
        SendMessage answer = new SendMessage(chat.getId().toString(), messageBuilder.toString());
        answer.setParseMode(ParseMode.MARKDOWN);
        try {
            telegramClient.execute(answer);
        } catch (TelegramApiException e) {
            log.error("Error", e);
        }
    }
}
