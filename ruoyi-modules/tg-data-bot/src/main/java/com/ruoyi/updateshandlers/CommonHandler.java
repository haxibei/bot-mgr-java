package com.ruoyi.updateshandlers;

import com.ruoyi.common.core.utils.DateUtils;
import com.ruoyi.utils.ThreadUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.ruoyi.constant.BtnCallCmd;
import com.ruoyi.constant.KeyBoardCmd;
import com.ruoyi.exception.TgMsgException;
import com.ruoyi.handlers.CmdUtil;
import com.ruoyi.handlers.ICmdHandler;
import com.ruoyi.handlers.model.BtnCmdCallData;
import com.ruoyi.handlers.model.CmdBean;
import com.ruoyi.handlers.model.ICmdInputHandler;
import org.telegram.telegrambots.meta.api.methods.botapimethods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.message.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

/**
 * This handler mainly works with commands to demonstrate the Commands feature of the API
 *
 * @author Timo Schulz (Mit0x2)
 */
@Slf4j
@Component
public class CommonHandler {

    @Autowired
    private CmdUtil cmdUtil;

    public void processMsg(TelegramClient telegramClient, Update update) {
        if (update.hasMessage()) {
            Message message = update.getMessage();

            if (message.getChatId() > 0 && !cmdUtil.getUserStateForCommandsBot(message.getFrom().getId())) {
                return;
            }

            //Y1123Bot 要求 只要在群里回复消息
//            String botId = ThreadUtil.getBotId();
//
//            if(message.getChatId() > 0 && "8132308359".equals(botId)) {
//                log.info("Y1123Bot 机器人不允许私聊");
//                return;
//            }

            if (message.hasText()) {
                KeyBoardCmd cmd = KeyBoardCmd.getByText(message.getText());

                if (cmd != null) {
                    try {
                        BotApiMethod echoMessage = cmd.getMsg(message, cmd.getBtnCmd());

                        try {
                            telegramClient.execute(echoMessage);
                        } catch (TelegramApiException e) {
                            log.error("Error processing non-command update", e);
                        }
                    } catch (Exception e) {
                        if(e instanceof TgMsgException) {
                            //TODo 统一处理长
                            //telegramClient.execute(echoMessage);
                        }else {
                            log.error("Error processing non-command update", e);
                        }
                    }
                }else {
                    //消息速率如果超过1.5秒一条就删除
                    boolean locked = cmdUtil.getLockState(message.getFrom().getId());
                    if(locked) {
                        try {
                            SendMessage msg = new SendMessage(message.getChat().getId().toString(), "\uD83C\uDE32手速太快了");
                            telegramClient.execute(msg);
                        } catch (TelegramApiException e) {
                            log.error("Error processing non-command update", e);
                        }
                        return;
                    }
                    cmdUtil.lockedTgMsg(message.getFrom().getId());
                    try {
                        //当前等待输入的 命令
                        CmdBean cmdBean = cmdUtil.getCmd(message.getChatId().toString());
                        if(cmdBean != null && StringUtils.isNotBlank(cmdBean.getCmdName())) {
                            BtnCallCmd btnCmd = BtnCallCmd.getByName(cmdBean.getCmdName());
                            ICmdInputHandler handler = btnCmd.getInputHandler();
                            handler.doBusiness(telegramClient, message, btnCmd, cmdBean.getParams());
                        }else {
                            String text = message.getText().trim().replaceAll("\\s+", " ");
                            String[] params = text.split(" ");

                            String date = null;
                            String agentCode = null;
                            if(params.length == 1 && DateUtils.parseDate(params[0]) != null) {
                                date = params[0];
                            }else if(params.length == 2) {
                                if(DateUtils.parseDate(params[0]) != null) {//2025-08-08 abcd1234
                                    date = params[0];
                                    agentCode = params[1];
                                }else if( DateUtils.parseDate(params[1]) != null) {//abcd1234 2025-08-08
                                    date = params[1];
                                    agentCode = params[0];
                                }

                            }
                            if(date != null) {
                                BtnCallCmd btnCmd = BtnCallCmd.QueryTextData;
                                ICmdHandler handler = btnCmd.getHandler();
                                BotApiMethod echoMessage = handler.dealMsg(telegramClient, message, btnCmd, date, agentCode);
                                if(echoMessage != null) {
                                    try {
                                        telegramClient.execute(echoMessage);
                                    } catch (TelegramApiException e) {
                                        log.error("Error processing non-command update", e);
                                    }
                                }
                            }else {
                                System.out.println("非法 不做处理: "+ message.getText());
                            }
                        }
                    }catch (Exception e) {
                        e.printStackTrace();
                        log.error("处理消息错误", e);
                    }finally {
                        cmdUtil.unLockedTgMsg(message.getFrom().getId());
                    }
                }
            }
        }else if(update.hasCallbackQuery()) {
            CallbackQuery callbackQuery = update.getCallbackQuery();
            String data = callbackQuery.getData();

            BtnCmdCallData cmdCallData = BtnCallCmd.parseCallData(data);

            BtnCallCmd cmd = BtnCallCmd.getByName(cmdCallData.getCmd());

            if(cmd != null) {
                ICmdHandler handler = cmd.getHandler();
                if(handler != null) {
                    Message message = (Message) callbackQuery.getMessage();
                    try {
                        BotApiMethod echoMessage = handler.dealMsg(telegramClient, message, cmd, cmdCallData.getParam());
                        if(echoMessage != null) {
                            try {
                                telegramClient.execute(echoMessage);
                            } catch (TelegramApiException e) {
                                log.error("Error processing non-command update", e);
                            }
                        }
                    } catch (Exception e) {
                        if(e instanceof TgMsgException) {
                            //TODo 统一处理i长
                            //telegramClient.execute(echoMessage);
                        }else {
                            log.error("bnt processing non-command update", e);
                        }
                    }
                }
            }
            System.out.println(data);
        }
    }

    public boolean isOneToNine(String str) {
        return str != null && str.matches("[1-9]");
    }
}