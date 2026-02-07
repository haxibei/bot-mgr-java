package com.ruoyi.system.api;

import com.ruoyi.common.core.constant.ServiceNameConstants;
import com.ruoyi.common.core.domain.R;
import com.ruoyi.system.api.factory.RemoteBotFallbackFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

/**
 * 落地页服务
 * 
 * @author ruoyi
 */
@FeignClient(contextId = "remoteBotService", value = ServiceNameConstants.TG_SERVICE, fallbackFactory = RemoteBotFallbackFactory.class)
public interface RemoteBotService
{

    @GetMapping("/job/spiderData")
    public R<String> spiderData(@RequestParam("date") String date);

    @GetMapping("/job/refreshTk")
    public R<String> refreshTk(@RequestParam("date") String date);

    @GetMapping("/job/pushDataMsg")
    public R<String> pushDataMsg(@RequestParam("date") String date);

    @GetMapping("/job/sendToGroupMsg")
    public R<String> sendToGroupMsg(@RequestParam("botId") String botId, @RequestParam("msg") String msg);


}
