package com.ruoyi.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.ruoyi.commands.CustomCommand;
import com.ruoyi.commands.StartCommand;
import com.ruoyi.common.core.domain.R;
import com.ruoyi.common.core.utils.StringUtils;
import com.ruoyi.common.mp.service.IBaseService;
import com.ruoyi.config.BotHandlerConfig;
import com.ruoyi.config.properties.BotConfig;
import com.ruoyi.db.domain.BotInfo;
import com.ruoyi.db.domain.DomainInfo;
import com.ruoyi.db.service.IBotInfoService;
import com.ruoyi.db.service.IDomainInfoService;
import com.ruoyi.updateshandlers.ISetCommand;
import com.ruoyi.web.base.BaseController;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.telegram.telegrambots.extensions.bots.commandbot.CommandBot;
import org.telegram.telegrambots.extensions.bots.commandbot.commands.ICommandRegistry;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 网站信息Controller
 * 
 * @author ruoyi
 * @date 2025-08-07
 */
@Api(tags = "网站信息")
@RestController
@RequestMapping("/domainInfo")
@Slf4j
public class DomainInfoController extends BaseController<DomainInfo>
{
    @Autowired
    private IDomainInfoService domainInfoService;

    @Autowired
    private BotHandlerConfig botHandlerConfig;

    @Autowired
    private IBotInfoService botInfoService;

    @Override
    public String getModule() {
        return "tg:domainInfo";
    }

    @Override
    public String getModuleName() {
        return "网站信息";
    }

    @Override
    public IBaseService<DomainInfo> getService() {
        return domainInfoService;
    }

    @Override
    protected R save(DomainInfo entity) {
        R ret = super.save(entity);

        if(StringUtils.isNotBlank(entity.getCommand())) {
            setCommand(entity);
        }

        return ret;
    }

    @Override
    protected R update(DomainInfo entity) {
        R ret = super.update(entity);

        if(StringUtils.isNotBlank(entity.getCommand())) {
            setCommand(entity);
        }
        return ret;
    }
    private void setCommand(DomainInfo entity) {
        setCommand(entity, false);
    }
    private void setCommand(DomainInfo entity, boolean isDel) {
        CommandBot handler = botHandlerConfig.getHandler(entity.getBotId());
        ICommandRegistry commandRegistry = (ICommandRegistry) handler;
        if (commandRegistry != null) {
            BotInfo bot = botInfoService.getById(entity.getBotId());
            BotConfig botConfig = new BotConfig();
            botConfig.setUser(bot.getBotUser());
            botConfig.setToken(bot.getBotToken());

            CustomCommand cmd = new CustomCommand(entity.getCommand(), entity.getCommand() + "站点", entity.getDomain(), botConfig);
            commandRegistry.deregister(cmd);
            if(!isDel) {
                commandRegistry.register(cmd);
            }
//            ISetCommand h = (ISetCommand) handler;
//            DomainInfo query = new DomainInfo();
//            query.setBotId(entity.getBotId());
//            QueryWrapper<DomainInfo> baseWrapper = domainInfoService.getBaseWrapper(query);
//            baseWrapper.lambda().isNotNull(DomainInfo::getCommand);
//
//            List<BotCommand> cmds = new ArrayList<>();
//            for (DomainInfo domainInfo : domainInfoService.list(baseWrapper)) {
//                cmds.add(new BotCommand(domainInfo.getCommand(), domainInfo.getCommand() + "站点"));
//            }
//            h.setCommands(cmds);
        }
    }

    @Override
    protected R delete(Long[] pks) {
        List<DomainInfo> domainInfos = domainInfoService.listByIds(Arrays.asList(pks));

        R ret = super.delete(pks);
        for (DomainInfo domainInfo : domainInfos) {
            if(StringUtils.isNotBlank(domainInfo.getCommand())) {
                setCommand(domainInfo, true);
            }
        }
        return ret;
    }

    @PostConstruct
    public void initLoadDomainConfig() {
        DomainInfo query = new DomainInfo();
        QueryWrapper<DomainInfo> baseWrapper = domainInfoService.getBaseWrapper(query);
        baseWrapper.lambda().isNotNull(DomainInfo::getCommand);

        List<DomainInfo> domains = domainInfoService.list(baseWrapper);

        Map<String/*botId*/, List<DomainInfo>> botDomains = domains.stream().collect(Collectors.groupingBy(DomainInfo::getBotId));

        botDomains.forEach((botId, botDomainList) -> {
            BotInfo bot = botInfoService.getById(botId);
            BotConfig botConfig = new BotConfig();
            botConfig.setUser(bot.getBotUser());
            botConfig.setToken(bot.getBotToken());

//            List<BotCommand> cmds = new ArrayList<>();
            List<CustomCommand> custCmds = new ArrayList<>();
            for (DomainInfo domainInfo : botDomainList) {
//                cmds.add(new BotCommand(domainInfo.getCommand(), domainInfo.getCommand() + "站点"));

                custCmds.add(new CustomCommand(domainInfo.getCommand(), domainInfo.getCommand() + "站点", domainInfo.getDomain(), botConfig));
            }
            CommandBot handler = botHandlerConfig.getHandler(botId);

            ICommandRegistry commandRegistry = (ICommandRegistry) handler;
            commandRegistry.registerAll(custCmds.toArray(new CustomCommand[0]));

//            ISetCommand h = (ISetCommand) botHandlerConfig.getHandler(botId);
//            h.setCommands(cmds);
        });

    }
}
