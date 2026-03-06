package com.ruoyi.commands;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.ruoyi.common.core.utils.SpringUtils;
import com.ruoyi.config.BotHandlerConfig;
import com.ruoyi.config.properties.BotConfig;
import com.ruoyi.db.domain.DomainInfo;
import com.ruoyi.db.service.IDomainInfoService;
import com.ruoyi.handlers.handlerImpl.DataInfoHandler;
import com.ruoyi.utils.ThreadUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import com.ruoyi.constant.KeyBoardCmd;
import com.ruoyi.handlers.CmdUtil;
import com.ruoyi.services.BotConfigService;
import org.telegram.telegrambots.extensions.bots.commandbot.CommandBot;
import org.telegram.telegrambots.extensions.bots.commandbot.commands.BotCommand;
import org.telegram.telegrambots.extensions.bots.commandbot.commands.IBotCommand;
import org.telegram.telegrambots.extensions.bots.commandbot.commands.ICommandRegistry;
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
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletableFuture;

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
        super("start", "使用或刷新机器人");
        this.botConfigService = SpringUtils.getBean(BotConfigService.class);
        this.cmdUtil = SpringUtils.getBean(CmdUtil.class);
        this.botConfig = botConfig;
    }

    @Override
    public void execute(TelegramClient telegramClient, User user, Chat chat, String[] strings) {
        ThreadUtil.clear();
        String botId = botConfig.getBotId();
        ThreadUtil.setBotId(botId);

        StringBuilder messageBuilder = new StringBuilder();

        String userName = user.getUserName();
        String nickName = (StringUtils.isBlank(user.getFirstName())?"":user.getFirstName())
                + (StringUtils.isBlank(user.getLastName())?"":user.getLastName());

        if (!cmdUtil.getUserStateForCommandsBot(user.getId())) {
            cmdUtil.setUserStateForCommandsBot(user.getId(), userName, nickName, (strings == null || strings.length == 0)?null:strings[0]);
        }

        //获取当前机器人的域名，如果是需要查询下级代理的数据， 就直接全部绑定顶级代理
        CompletableFuture.runAsync(() -> {
            try {
                doBindAgent(botId, user.getId());
            } catch (Exception e) {
                log.error("执行初始化绑定顶级代理失败", e);
            }
        });

        SendMessage answer = new SendMessage(chat.getId().toString(), messageBuilder.toString());
        if("8571189674".equals(botConfig.getBotId())) {//这里先写死
            IDomainInfoService domainInfoService = SpringUtils.getBean(IDomainInfoService.class);
            DomainInfo query = new DomainInfo();
            QueryWrapper<DomainInfo> baseWrapper = domainInfoService.getBaseWrapper(query);
            baseWrapper.lambda().isNotNull(DomainInfo::getCommand);
            List<DomainInfo> domains = domainInfoService.list(baseWrapper);
            StringBuilder cmds = new StringBuilder();
            String demoCmd = "ph879";
            for (DomainInfo domain : domains) {
                cmds.append("• ").append(domain.getCommand()).append("\n");
                demoCmd = domain.getCommand();
            }

            messageBuilder.append("\uD83C\uDFE0 数据查询机器人\n" +
                    "用于查询代理账号数据\n" +
                    "\n" +
                    "━━━━━━━━━━━━━━\n" +
                    "\n" +
                    "\uD83D\uDD0E 当前已接入站点\n" +
                    cmds +
                    "\n" +
                    "━━━━━━━━━━━━━━\n" +
                    "\n" +
                    "\uD83D\uDCCA 查当日数据\n" +
                    "/"+demoCmd+" 代理账号\n" +
                    "例：/"+demoCmd+" xx2052\n" +
                    "\n" +
                    "\uD83D\uDCC6 查指定日期\n" +
                    "/"+demoCmd+" 代理账号 YYYY-MM-DD\n" +
                    "例：/"+demoCmd+" xx2052 2026-02-17\n" +
                    "\n" +
                    "\uD83D\uDCC5 查指定月份\n" +
                    "/"+demoCmd+" 代理账号 YYYY-MM\n" +
                    "例：/"+demoCmd+" xx2052 2026-02\n" +
                    "\n" +
                    "━━━━━━━━━━━━━━"
            );

            answer = new SendMessage(chat.getId().toString(), messageBuilder.toString());
        }else {
            messageBuilder.append("\uD83C\uDFE0 欢迎使用TG数据查询机器人！本机器人提供以下服务：\n" +
                            "\n" +
                            "▪\uFE0F 查询代理数据\n"
//                +"⚡\uFE0F如需帮助，请联系客服  " + botConfigService.getKefu()
            );
            answer = new SendMessage(chat.getId().toString(), messageBuilder.toString());

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
        }

        try {
            telegramClient.execute(answer);
        } catch (TelegramApiException e) {
            log.error("Error", e);
        }
    }

    private void doBindAgent(String botId, Long userId) {

        DataInfoHandler handler = SpringUtils.getBean(DataInfoHandler.class);

        handler.bindAgent(botId, userId);
    }
}
