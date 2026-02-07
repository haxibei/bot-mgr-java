package com.ruoyi.commands;

import com.ruoyi.common.core.utils.SpringUtils;
import lombok.extern.slf4j.Slf4j;
import com.ruoyi.handlers.CmdUtil;
import org.telegram.telegrambots.extensions.bots.commandbot.commands.BotCommand;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.chat.Chat;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

/**
 * This commands stops the conversation with the bot.
 * Bot won't respond to user until he sends a start command
 *
 * @author Timo Schulz (Mit0x2)
 */
@Slf4j
public class StopCommand extends BotCommand {

    public static final String LOGTAG = "STOPCOMMAND";

    private CmdUtil cmdUtil;
    /**
     * Construct
     */
    public StopCommand() {
        super("stop", "With this command you can stop the Bot");
        this.cmdUtil = SpringUtils.getBean(CmdUtil.class);
    }

    @Override
    public void execute(TelegramClient telegramClient, User user, Chat chat, String[] arguments) {
        if (cmdUtil.getUserStateForCommandsBot(user.getId())) {
//            cmdUtil.setUserStateForCommandsBot(user.getId(), false);
            String userName = user.getFirstName() + " " + user.getLastName();

            SendMessage answer = new SendMessage(chat.getId().toString(), "Good bye " + userName + "\n" + "Hope to see you soon!");

            try {
                telegramClient.execute(answer);
            } catch (TelegramApiException e) {
                log.error("Error", e);
            }
        }
    }
}
