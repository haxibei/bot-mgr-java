package com.ruoyi.task;

import com.ruoyi.common.core.utils.StringUtils;
import com.ruoyi.common.redis.service.RedisService;
import com.ruoyi.config.BotClient;
import com.ruoyi.constant.RedisChannel;
import com.ruoyi.constant.TgType;
import com.ruoyi.db.domain.MsgInfo;
import com.ruoyi.db.domain.TgInfo;
import com.ruoyi.db.service.IMsgInfoService;
import com.ruoyi.db.service.ITgInfoService;
import com.ruoyi.model.TgMsgContent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.ForwardMessage;
import org.telegram.telegrambots.meta.api.methods.ForwardMessages;
import org.telegram.telegrambots.meta.api.methods.botapimethods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMediaGroup;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.media.InputMedia;
import org.telegram.telegrambots.meta.api.objects.media.InputMediaPhoto;
import org.telegram.telegrambots.meta.api.objects.media.InputMediaVideo;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

/**
 * 定时任务调度测试
 * 
 * @author ruoyi
 */
@Slf4j
@Component("msgTask")
public class MsgTask
{
    @Autowired
    private IMsgInfoService msgInfoService;

    @Autowired
    private ITgInfoService tgInfoService;

    @Autowired
    private RedisService redisService;

    public void push(String msgId)
    {
        MsgInfo msgInfo = msgInfoService.getById(msgId);

        int type = 0;//发送消息
        String fromChatId = null;
        String messageId = null;
        if(msgInfo.getContent().startsWith("https://t.me/")) {//转发消息
            type = 1;//转发消息
            String link = msgInfo.getContent().replaceAll("https://t.me/", "");

            String[] arr = link.split("/");

            if(arr.length == 3) {// https://t.me/c/3283790069/19
                String groupId = arr[1];
                messageId = arr[2];
                fromChatId = "-100" + groupId;
            }else {
                String userName = arr[0];
                messageId = arr[1];
                fromChatId = userName;
            }
        }
        TgInfo tgInfo = tgInfoService.getById(msgInfo.getTgId());
        String[] chatIds = msgInfo.getChatIds().split(",");
        for(String chatId : chatIds) {
            TgMsgContent msgContent = new TgMsgContent();
            msgContent.setChatId(chatId);
            msgContent.setImgList(msgInfo.getImgList());
            msgContent.setVideoList(msgInfo.getVideoList());
            msgContent.setType(type);
            if(type == 0) {
                msgContent.setMsg(msgInfo.getContent());
            }else {
                msgContent.setFromChatId(fromChatId);
                msgContent.setTgMsgId(messageId);
            }
            if(TgType.account.equals(tgInfo.getTgType())) {//个号推送
                redisService.convertAndSend(RedisChannel.tgGroupMsgChannel+"_"+msgInfo.getTgId(), msgContent);
            }else {//机器人推送
                TelegramClient client = BotClient.getClient(tgInfo.getTgId(), tgInfo.getBotToken());

                sendBotMsg(client, msgContent);
            }
        }
        //调用 进行消息推送
        log.info("执行消息推送" + msgInfo);
    }

    private void sendBotMsg(TelegramClient client, TgMsgContent msgContent) {
        if(msgContent.getType() == 1) {//转发消息
            Set<Integer> msgIds = StringUtils.toIntSet(msgContent.getTgMsgId());
            ForwardMessages message = new ForwardMessages(msgContent.getChatId(), msgContent.getFromChatId(), msgIds.stream().toList());
            send(client, message);
        }else {

            if(StringUtils.isNotBlank(msgContent.getImgList())
                    || StringUtils.isNotBlank(msgContent.getVideoList())) {
                List<InputMedia> medias = new ArrayList<>();
                if(StringUtils.isNotBlank(msgContent.getImgList())) {
                    String[] images = msgContent.getImgList().split(";");
                    for (int i = 0; i < images.length; i++) {
                        InputMediaPhoto photo = new InputMediaPhoto(new File(images[i]), "image" + i);
                        photo.setCaption(msgContent.getMsg()); // 可选，media group 里只有一个元素能带 caption，一般放第一项
                        medias.add(photo);
                    }
                }
                if(StringUtils.isNotBlank(msgContent.getVideoList())) {
                    String[] videos = msgContent.getVideoList().split(";");
                    for (int i = 0; i < videos.length; i++) {
                        InputMediaVideo video = new InputMediaVideo(new File(videos[i]), "video"+i);
                        medias.add(video);
                    }
                }
                SendMediaGroup message = new SendMediaGroup(msgContent.getChatId(), medias);
                try {
                    client.execute(message);
                } catch (TelegramApiException e) {
                    throw new RuntimeException(e);
                }
            }else {
                SendMessage message = new SendMessage(msgContent.getChatId(), msgContent.getMsg());
                send(client, message);
            }
        }
    }

    private void send(TelegramClient client, BotApiMethod message) {
        try {
            client.execute(message);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }
}
