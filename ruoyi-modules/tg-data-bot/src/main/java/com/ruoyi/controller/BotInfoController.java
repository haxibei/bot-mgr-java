package com.ruoyi.controller;

import com.ruoyi.commands.StartCommand;
import com.ruoyi.commands.StartRecvMsgCommand;
import com.ruoyi.commands.StopRecvMsgCommand;
import com.ruoyi.common.core.domain.R;
import com.ruoyi.common.mp.service.IBaseService;
import com.ruoyi.config.BotClientConfig;
import com.ruoyi.config.BotHandlerConfig;
import com.ruoyi.config.properties.BotConfig;
import com.ruoyi.config.properties.BotProperties;
import com.ruoyi.db.domain.BotInfo;
import com.ruoyi.db.service.IBotInfoService;
import com.ruoyi.updateshandlers.CommonHandler;
import com.ruoyi.updateshandlers.PollHandler;
import com.ruoyi.updateshandlers.WebHookHandler;
import com.ruoyi.web.base.BaseController;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient;
import org.telegram.telegrambots.extensions.bots.commandbot.commands.ICommandRegistry;

/**
 * 机器人信息Controller
 * 
 * @author ruoyi
 * @date 2026-02-21
 */
@Api(tags = "机器人信息")
@RestController
@RequestMapping("/botInfo")
public class BotInfoController extends BaseController<BotInfo>
{
    @Autowired
    private IBotInfoService botInfoService;

    @Autowired
    private BotProperties botProperties;

    @Autowired
    private BotClientConfig botClientConfig;

    @Autowired
    private BotHandlerConfig botHandlerConfig;

    @Autowired
    private CommonHandler commonHandler;

    @Override
    public String getModule() {
        return "tg:botInfo";
    }

    @Override
    public String getModuleName() {
        return "机器人信息";
    }

    @Override
    public IBaseService<BotInfo> getService() {
        return botInfoService;
    }

    @Override
    protected R save(BotInfo entity) {
        String botId = entity.getBotToken().split(":")[0];
        entity.setBotId(Long.parseLong(botId));
        R ret = super.save(entity);

        botProperties.addBot(entity.getBotId(), entity.getBotToken(), entity.getBotUser());

        OkHttpTelegramClient client = new OkHttpTelegramClient(entity.getBotToken());
        botClientConfig.putClient(entity.getBotId().toString(), client);

        BotConfig botConfig = new BotConfig();
        botConfig.setUser(entity.getBotUser());
        botConfig.setToken(entity.getBotToken());
        if ("webhook".equals(botProperties.getModel())) {
            WebHookHandler handler = new WebHookHandler(client, botConfig, botProperties.getWebhookUrl(), commonHandler);
            handler.runSetWebhook();
            botHandlerConfig.putHandler(entity.getBotId().toString(), handler);

        }else {
            PollHandler handler = new PollHandler(botClientConfig.getClient(botConfig.getBotId()), botConfig, commonHandler);
            botHandlerConfig.putHandler(entity.getBotId().toString(), handler);
        }
        return ret;
    }

    @Override
    protected R update(BotInfo entity) {
        R ret = super.update(entity);

        botProperties.addBot(entity.getBotId(), entity.getBotToken(), entity.getBotUser());

        OkHttpTelegramClient client = new OkHttpTelegramClient(entity.getBotToken());
        botClientConfig.putClient(entity.getBotId().toString(), client);

        BotConfig botConfig = new BotConfig();
        botConfig.setUser(entity.getBotUser());
        botConfig.setToken(entity.getBotToken());

        if ("webhook".equals(botProperties.getModel())) {
            WebHookHandler handler = new WebHookHandler(client, botConfig, botProperties.getWebhookUrl(), commonHandler);
            handler.runSetWebhook();
            botHandlerConfig.putHandler(entity.getBotId().toString(), handler);

        }else {
            PollHandler handler = new PollHandler(botClientConfig.getClient(botConfig.getBotId()), botConfig, commonHandler);
            botHandlerConfig.putHandler(entity.getBotId().toString(), handler);
        }
        return ret;
    }
}
