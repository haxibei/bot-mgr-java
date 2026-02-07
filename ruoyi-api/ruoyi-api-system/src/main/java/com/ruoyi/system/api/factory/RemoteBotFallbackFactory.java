package com.ruoyi.system.api.factory;

import com.ruoyi.common.core.domain.R;
import com.ruoyi.system.api.RemoteBotService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

/**
 * 落地页服务降级处理
 * 
 * @author ruoyi
 */
@Component
public class RemoteBotFallbackFactory implements FallbackFactory<RemoteBotService>
{
    private static final Logger log = LoggerFactory.getLogger(RemoteBotFallbackFactory.class);

    @Override
    public RemoteBotService create(Throwable throwable)
    {
        log.error("机器人服务调用失败:{}", throwable.getMessage());
        return new RemoteBotService()
        {
            @Override
            public R<String> spiderData(String date) {
                return R.fail("执行查询数据任务失败:" + throwable.getMessage());
            }

            @Override
            public R<String> refreshTk(String date) {
                return R.fail("执行刷新tk任务失败:" + throwable.getMessage());
            }

            @Override
            public R<String> pushDataMsg(String date) {
                return R.fail("执行推送数据任务失败:" + throwable.getMessage());
            }

            @Override
            public R<String> sendToGroupMsg(String botId, String msg) {
                return R.fail("执行推送群组提醒消息失败:" + throwable.getMessage());
            }
        };
    }
}
