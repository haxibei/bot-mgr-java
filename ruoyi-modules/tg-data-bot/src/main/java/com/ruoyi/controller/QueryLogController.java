package com.ruoyi.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.ruoyi.common.core.domain.R;
import com.ruoyi.common.core.utils.poi.ExcelUtil;
import com.ruoyi.common.entity.PageData;
import com.ruoyi.common.log.annotation.Log;
import com.ruoyi.common.log.enums.BusinessType;
import com.ruoyi.common.mp.service.IBaseService;
import com.ruoyi.common.security.annotation.RequiresPermissions;
import com.ruoyi.config.BotClientConfig;
import com.ruoyi.config.BotHandlerConfig;
import com.ruoyi.config.properties.BotConfig;
import com.ruoyi.config.properties.BotProperties;
import com.ruoyi.db.domain.BotInfo;
import com.ruoyi.db.domain.QueryBlack;
import com.ruoyi.db.domain.QueryLog;
import com.ruoyi.db.service.IBotInfoService;
import com.ruoyi.db.service.IQueryBlackService;
import com.ruoyi.db.service.IQueryLogService;
import com.ruoyi.updateshandlers.CommonHandler;
import com.ruoyi.updateshandlers.PollHandler;
import com.ruoyi.updateshandlers.WebHookHandler;
import com.ruoyi.web.base.BaseController;
import io.swagger.annotations.Api;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient;

import javax.servlet.http.HttpServletResponse;
import java.util.*;

/**
 * 机器人信息Controller
 * 
 * @author ruoyi
 * @date 2026-02-21
 */
@Api(tags = "机器人信息")
@RestController
@RequestMapping("/queryLog")
public class QueryLogController extends BaseController<QueryLog>
{
    @Autowired
    private IQueryLogService queryLogService;

    @Autowired
    private IQueryBlackService queryBlackService;

    @Override
    public String getModule() {
        return "tg:queryLog";
    }

    @Override
    public String getModuleName() {
        return "查询日志";
    }

    @Override
    public IBaseService<QueryLog> getService() {
        return queryLogService;
    }

    @Override
    protected R<PageData<QueryLog>> listData(QueryLog queryData) {
        queryData.setOrdseg("create_time.desc");
        QueryWrapper<QueryLog> queryWrapper = getQueryWrapper(queryData);
        if(Objects.equals(queryData.getInBlack(), "1")) {
            queryWrapper.lambda().inSql(QueryLog::getTgId, "select tg_id from query_black");
        }
        PageData<QueryLog> pageData = queryLogService.selectPage(queryData.getPageInfo(), queryWrapper);

        if(CollectionUtils.isNotEmpty(pageData.getRows())) {
            if(Objects.equals(queryData.getInBlack(), "1")) {
                pageData.getRows().forEach(queryLog -> {
                    queryLog.setInBlack("1");
                });
            }else {
                List<String> tgIds = new ArrayList<>();
                pageData.getRows().forEach(queryLog -> {
                    tgIds.add(queryLog.getTgId());
                });

                QueryBlack queryBlack = new QueryBlack();
//            queryBlack.setBotId();
                QueryWrapper<QueryBlack> baseWrapper = queryBlackService.getBaseWrapper(queryBlack);
                baseWrapper.lambda().in(QueryBlack::getTgId, tgIds);
                List<QueryBlack> blacks = queryBlackService.list(baseWrapper);
                if(CollectionUtils.isNotEmpty(blacks)) {
                    Set<String> tgIdSet = new HashSet<>();
                    blacks.forEach(black -> {
                        tgIdSet.add(black.getTgId());
                    });

                    pageData.getRows().forEach(queryLog -> {
                        queryLog.setInBlack(tgIdSet.contains(queryLog.getTgId())?"1":"0");
                    });
                }
            }
        }
        return R.ok(pageData);
    }

    @Log(title = "添加黑名单", businessType = BusinessType.INSERT)
    @PostMapping("/addBlack/{tgId}")
    public void addBlack(@PathVariable("tgId") String tgId) {
        QueryBlack queryBlack = new QueryBlack();
        queryBlack.setTgId(tgId);
        queryBlackService.insertOrUpdate(queryBlack);
    }

    @Log(title = "移除黑名单", businessType = BusinessType.DELETE)
    @PostMapping("/rmBlack/{tgId}")
    public void rmBlack(@PathVariable("tgId") String tgId) {
        QueryBlack queryBlack = new QueryBlack();
        queryBlack.setTgId(tgId);
        QueryWrapper<QueryBlack> baseWrapper = queryBlackService.getBaseWrapper(queryBlack);
        queryBlackService.remove(baseWrapper);
    }
}
