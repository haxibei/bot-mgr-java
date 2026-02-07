package com.ruoyi.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.ruoyi.common.core.constant.Constants;
import com.ruoyi.common.core.domain.R;
import com.ruoyi.common.core.utils.StringUtils;
import com.ruoyi.common.entity.PageData;
import com.ruoyi.common.log.annotation.Log;
import com.ruoyi.common.log.enums.BusinessType;
import com.ruoyi.common.mp.service.IBaseService;
import com.ruoyi.common.security.annotation.RequiresPermissions;
import com.ruoyi.common.security.utils.SecurityUtils;
import com.ruoyi.constant.RedisChannel;
import com.ruoyi.db.domain.GroupInfo;
import com.ruoyi.db.domain.MsgInfo;
import com.ruoyi.db.service.IGroupInfoService;
import com.ruoyi.db.service.IMsgInfoService;
import com.ruoyi.model.TgMsgContent;
import com.ruoyi.quartz.util.CronUtils;
import com.ruoyi.quartz.util.ScheduleUtils;
import com.ruoyi.web.base.BaseController;
import io.swagger.annotations.Api;
import org.apache.commons.collections4.CollectionUtils;
import org.quartz.SchedulerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.PatternMatchUtils;
import org.springframework.web.bind.annotation.*;

import java.sql.Array;
import java.util.*;

/**
 * 网站信息Controller
 * 
 * @author ruoyi
 * @date 2025-08-07
 */
@Api(tags = "个号信息")
@RestController
@RequestMapping("/msgInfo")
public class MsgInfoController extends BaseController<MsgInfo>
{
    @Autowired
    private IMsgInfoService msgInfoService;

    @Autowired
    private IGroupInfoService groupInfoService;

    @Override
    public String getModule() {
        return "tgPush:msgInfo";
    }

    @Override
    public String getModuleName() {
        return "信息";
    }

    @Override
    public IBaseService<MsgInfo> getService() {
        return msgInfoService;
    }

    @Override
    protected boolean ignoreScope() {
        return false;
    }

    @Override
    public R save(MsgInfo job) {
        if (!CronUtils.isValid(job.getCronExpression())){
            return R.fail("新增任务'" + job.getJobName() + "'失败，Cron表达式不正确");
        }else if (StringUtils.isBlank(job.getChatIds())){
            return R.fail("新增任务'" + job.getJobName() + "'失败，请选择需要发送消息的群组");
        }else if (StringUtils.isBlank(job.getContent())){
            return R.fail("新增任务'" + job.getJobName() + "'失败，请选择需要发送消息的内容或转发的链接");
        }
        String chatIds = job.getChatIds();
        job.setChatIds(removeSame(chatIds));
        return R.ok(msgInfoService.save(job));
    }

    private String removeSame(String chatIds) {
        String[] chatIdArr = chatIds.split(",");
        Set<String> arr = new HashSet<>();
        Collections.addAll(arr, chatIdArr);
        return StringUtils.join(arr, ",");
    }

    @Override
    public R update(MsgInfo job) {
        if (!CronUtils.isValid(job.getCronExpression())){
            return R.fail("修改任务'" + job.getJobName() + "'失败，Cron表达式不正确");
        }else if (StringUtils.isBlank(job.getChatIds())){
            return R.fail("新增任务'" + job.getJobName() + "'失败，请选择需要发送消息的群组");
        }else if (StringUtils.isBlank(job.getContent())){
            return R.fail("新增任务'" + job.getJobName() + "'失败，请选择需要发送消息的内容或转发的链接");
        }
        String chatIds = job.getChatIds();
        job.setChatIds(removeSame(chatIds));
        return R.ok(msgInfoService.updateById(job));
    }

    /**
     * 定时任务状态修改
     */
    @RequiresPermissions("monitor:job:changeStatus")
    @Log(title = "定时任务", businessType = BusinessType.UPDATE)
    @PutMapping("/changeStatus")
    public R changeStatus(@RequestBody MsgInfo job) throws SchedulerException
    {
        MsgInfo newJob = msgInfoService.getById(job.getJobId());
        newJob.setStatus(job.getStatus());
        return R.ok(msgInfoService.changeStatus(newJob));
    }

    /**
     * 定时任务立即执行一次
     */
    @RequiresPermissions("monitor:job:changeStatus")
    @Log(title = "定时任务", businessType = BusinessType.UPDATE)
    @PutMapping("/run")
    public R run(@RequestBody MsgInfo job) throws SchedulerException
    {
        boolean result = msgInfoService.run(job);
        return result ? R.ok() : R.fail("任务不存在或已过期！");
    }

    @Override
    protected R<PageData<MsgInfo>> listData(MsgInfo queryData) {
        QueryWrapper<MsgInfo> queryWrapper = getQueryWrapper(queryData);

        Set<String> roles = SecurityUtils.getLoginUser().getRoles();
        boolean hasRole = roles.stream().filter(StringUtils::hasText)
                .anyMatch(x -> PatternMatchUtils.simpleMatch(x, "tg_push"));
        if(hasRole) {
            queryWrapper.lambda().inSql(MsgInfo::getTgId, "select tg_id from tg_info where owner_uid = " + SecurityUtils.getUserId());
        }
        PageData<MsgInfo> pageData = getService().selectPage(queryData.getPageInfo(), queryWrapper);
        return R.ok(pageData);
    }

    @GetMapping("/listAllGroup")
    public R<List<GroupInfo>> listAllGroup(String tgId) {

        GroupInfo query = new GroupInfo();
        query.setTgId(tgId);
        List<GroupInfo> groupInfos = groupInfoService.selectList(query);

        return R.ok(groupInfos);
    }
}
