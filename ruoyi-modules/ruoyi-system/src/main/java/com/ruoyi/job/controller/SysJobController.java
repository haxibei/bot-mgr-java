package com.ruoyi.job.controller;

import com.ruoyi.common.core.constant.Constants;
import com.ruoyi.common.core.domain.R;
import com.ruoyi.common.core.exception.job.TaskException;
import com.ruoyi.common.core.utils.StringUtils;
import com.ruoyi.common.core.utils.poi.ExcelUtil;
import com.ruoyi.common.entity.PageData;
import com.ruoyi.common.log.annotation.Log;
import com.ruoyi.common.log.enums.BusinessType;
import com.ruoyi.common.mp.service.IBaseService;
import com.ruoyi.common.security.annotation.RequiresPermissions;
import com.ruoyi.job.domain.SysJob;
import com.ruoyi.job.service.ISysJobService;
import com.ruoyi.quartz.util.CronUtils;
import com.ruoyi.quartz.util.ScheduleUtils;
import com.ruoyi.web.base.BaseController;
import org.quartz.SchedulerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 调度任务信息操作处理
 * 
 * @author ruoyi
 */
@RestController
@RequestMapping("/job")
public class SysJobController extends BaseController<SysJob>
{
    @Autowired
    private ISysJobService jobService;

    @Override
    public R<PageData<SysJob>> list(SysJob queryData) {
        queryData.setMisfirePolicy(null);
        return super.list(queryData);
    }

    /**
     * 导出定时任务列表
     */
    @RequiresPermissions("monitor:job:export")
    @Log(title = "定时任务", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, SysJob sysJob)
    {
        List<SysJob> list = jobService.selectJobList(sysJob);
        ExcelUtil<SysJob> util = new ExcelUtil<SysJob>(SysJob.class);
        util.exportExcel(response, list, "定时任务");
    }

    @Override
    public R save(SysJob job) {
        if (!CronUtils.isValid(job.getCronExpression()))
        {
            return R.fail("新增任务'" + job.getJobName() + "'失败，Cron表达式不正确");
        }
        else if (StringUtils.containsIgnoreCase(job.getInvokeTarget(), Constants.LOOKUP_RMI))
        {
            return R.fail("新增任务'" + job.getJobName() + "'失败，目标字符串不允许'rmi'调用");
        }
        else if (StringUtils.containsAnyIgnoreCase(job.getInvokeTarget(), new String[] { Constants.LOOKUP_LDAP, Constants.LOOKUP_LDAPS }))
        {
            return R.fail("新增任务'" + job.getJobName() + "'失败，目标字符串不允许'ldap(s)'调用");
        }
        else if (StringUtils.containsAnyIgnoreCase(job.getInvokeTarget(), new String[] { Constants.HTTP, Constants.HTTPS }))
        {
            return R.fail("新增任务'" + job.getJobName() + "'失败，目标字符串不允许'http(s)'调用");
        }
        else if (StringUtils.containsAnyIgnoreCase(job.getInvokeTarget(), Constants.JOB_ERROR_STR))
        {
            return R.fail("新增任务'" + job.getJobName() + "'失败，目标字符串存在违规");
        }
        else if (!ScheduleUtils.whiteList(job.getInvokeTarget()))
        {
            return R.fail("新增任务'" + job.getJobName() + "'失败，目标字符串不在白名单内");
        }

        try {
            return R.ok(jobService.insertJob(job));
        } catch (SchedulerException e) {
            throw new RuntimeException(e);
        } catch (TaskException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public R update(SysJob job) {
        if (!CronUtils.isValid(job.getCronExpression()))
        {
            return R.fail("修改任务'" + job.getJobName() + "'失败，Cron表达式不正确");
        }
        else if (StringUtils.containsIgnoreCase(job.getInvokeTarget(), Constants.LOOKUP_RMI))
        {
            return R.fail("修改任务'" + job.getJobName() + "'失败，目标字符串不允许'rmi'调用");
        }
        else if (StringUtils.containsAnyIgnoreCase(job.getInvokeTarget(), new String[] { Constants.LOOKUP_LDAP, Constants.LOOKUP_LDAPS }))
        {
            return R.fail("修改任务'" + job.getJobName() + "'失败，目标字符串不允许'ldap(s)'调用");
        }
        else if (StringUtils.containsAnyIgnoreCase(job.getInvokeTarget(), new String[] { Constants.HTTP, Constants.HTTPS }))
        {
            return R.fail("修改任务'" + job.getJobName() + "'失败，目标字符串不允许'http(s)'调用");
        }
        else if (StringUtils.containsAnyIgnoreCase(job.getInvokeTarget(), Constants.JOB_ERROR_STR))
        {
            return R.fail("修改任务'" + job.getJobName() + "'失败，目标字符串存在违规");
        }
        else if (!ScheduleUtils.whiteList(job.getInvokeTarget()))
        {
            return R.fail("修改任务'" + job.getJobName() + "'失败，目标字符串不在白名单内");
        }
        try {
            return R.ok(jobService.updateJob(job));
        } catch (SchedulerException e) {
            throw new RuntimeException(e);
        } catch (TaskException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 定时任务状态修改
     */
    @RequiresPermissions("monitor:job:changeStatus")
    @Log(title = "定时任务", businessType = BusinessType.UPDATE)
    @PutMapping("/changeStatus")
    public R changeStatus(@RequestBody SysJob job) throws SchedulerException
    {
        SysJob newJob = jobService.selectJobById(job.getJobId());
        newJob.setStatus(job.getStatus());
        return R.ok(jobService.changeStatus(newJob));
    }

    /**
     * 定时任务立即执行一次
     */
    @RequiresPermissions("monitor:job:changeStatus")
    @Log(title = "定时任务", businessType = BusinessType.UPDATE)
    @PutMapping("/run")
    public R run(@RequestBody SysJob job) throws SchedulerException
    {
        boolean result = jobService.run(job);
        return result ? R.ok() : R.fail("任务不存在或已过期！");
    }

    @Override
    public String getModule() {
        return "monitor:job";
    }

    @Override
    public String getModuleName() {
        return "定时任务";
    }

    @Override
    public IBaseService getService() {
        return jobService;
    }
}
