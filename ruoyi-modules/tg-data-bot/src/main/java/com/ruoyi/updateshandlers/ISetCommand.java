package com.ruoyi.updateshandlers;

import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;

import java.util.List;

public interface ISetCommand {

    public void setCommands(List<BotCommand> cmds);

}
