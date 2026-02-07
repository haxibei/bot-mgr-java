package com.ruoyi.commands;

import com.ruoyi.common.core.utils.SpringUtils;
import com.ruoyi.config.properties.BotConfig;
import com.ruoyi.utils.ThreadUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import com.ruoyi.constant.KeyBoardCmd;
import com.ruoyi.handlers.CmdUtil;
import com.ruoyi.services.BotConfigService;
import org.telegram.telegrambots.extensions.bots.commandbot.commands.BotCommand;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.chat.Chat;
import org.telegram.telegrambots.meta.api.objects.User;
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
public class StartCommand extends BotCommand {

    private CmdUtil cmdUtil;

    private BotConfigService botConfigService;

    private BotConfig botConfig;

    public StartCommand(BotConfig botConfig) {
        super("start", "With this command you can start the Bot");
        this.botConfigService = SpringUtils.getBean(BotConfigService.class);
        this.cmdUtil = SpringUtils.getBean(CmdUtil.class);
        this.botConfig = botConfig;
    }

    @Override
    public void execute(TelegramClient telegramClient, User user, Chat chat, String[] strings) {
        ThreadUtil.clear();
        ThreadUtil.setBotId(botConfig.getBotId());

        StringBuilder messageBuilder = new StringBuilder();

        String userName = user.getUserName();
        String nickName = (StringUtils.isBlank(user.getFirstName())?"":user.getFirstName())
                + (StringUtils.isBlank(user.getLastName())?"":user.getLastName());

        if (!cmdUtil.getUserStateForCommandsBot(user.getId())) {
            cmdUtil.setUserStateForCommandsBot(user.getId(), userName, nickName, (strings == null || strings.length == 0)?null:strings[0]);
        }
        messageBuilder.append("\uD83C\uDFE0 欢迎使用TG数据查询机器人！本机器人提供以下服务：\n" +
                "\n" +
                "▪\uFE0F 查询代理数据\n"
//                +"⚡\uFE0F如需帮助，请联系客服  " + botConfigService.getKefu()
                )
        ;

        SendMessage answer = new SendMessage(chat.getId().toString(), messageBuilder.toString());

        List<KeyboardRow> rows = new ArrayList<>();
        KeyboardRow row = new KeyboardRow();
        row.add(new KeyboardButton(KeyBoardCmd.AgentList.getText()));
        row.add(new KeyboardButton(KeyBoardCmd.AgentAdd.getText()));
        row.add(new KeyboardButton(KeyBoardCmd.AgentDel.getText()));
        rows.add(row);

        row = new KeyboardRow();
        row.add(new KeyboardButton(KeyBoardCmd.QueryData.getText()));
        row.add(new KeyboardButton(KeyBoardCmd.QueryToday.getText()));
        row.add(new KeyboardButton(KeyBoardCmd.QueryCustom.getText()));
        rows.add(row);

        ReplyKeyboardMarkup markup = new ReplyKeyboardMarkup(rows);
        markup.setResizeKeyboard(true);
        answer.setReplyMarkup(markup);
        answer.setParseMode(ParseMode.MARKDOWN);
        try {
            telegramClient.execute(answer);
        } catch (TelegramApiException e) {
            log.error("Error", e);
        }
    }
}
