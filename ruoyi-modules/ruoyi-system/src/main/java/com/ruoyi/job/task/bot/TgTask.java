package com.ruoyi.job.task.bot;

import com.ruoyi.common.core.domain.R;
import com.ruoyi.common.core.utils.DateUtils;
import com.ruoyi.common.core.utils.StringUtils;
import com.ruoyi.system.api.RemoteBotService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * 定时任务调度测试
 * 
 * @author ruoyi
 */
@Component("tgTask")
@Slf4j
public class TgTask
{
    @Autowired
    private RemoteBotService remoteBotService;

    public void spiderData(String date){
        if(StringUtils.isEmpty(date)) {
            date = DateUtils.getYesterDay();
        }
        log.info("执行 获取数据的job {}", date);
        R<String> ret = remoteBotService.spiderData(date);
        log.info("spiderData job execute ret {}", ret.getData());
    }

    public void refreshTk(String date){
        if(StringUtils.isEmpty(date)) {
            date = DateUtils.getDate();
        }
        log.info("执行 刷新tk的job {}", date);
        R<String> ret = remoteBotService.refreshTk(date);
        log.info("refreshTk job execute ret {}", ret.getData());
    }

    public void pushDataMsg(String date){
        if(StringUtils.isEmpty(date)) {
            date = DateUtils.getYesterDay();
        }
        log.info("执行 推送统计数据的job {}", date);
        R<String> ret = remoteBotService.pushDataMsg(date);
        log.info("pushDataMsg job execute ret {}", ret.getData());
    }

    public void sendToGroupMsg(String botId, String msg){
        log.info("执行 推送提醒消息的job {} {}", botId,  msg);
        R<String> ret = remoteBotService.sendToGroupMsg(botId, msg);
        log.info("sendToGroupMsg job execute ret {}", ret.getData());
    }

}
