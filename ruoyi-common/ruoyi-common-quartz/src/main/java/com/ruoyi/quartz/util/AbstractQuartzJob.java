package com.ruoyi.quartz.util;

import com.alibaba.fastjson2.JSON;
import com.ruoyi.common.core.constant.ScheduleConstants;
import com.ruoyi.common.core.utils.ExceptionUtil;
import com.ruoyi.common.core.utils.SpringUtils;
import com.ruoyi.common.core.utils.StringUtils;
import com.ruoyi.common.core.utils.bean.BeanUtils;
import com.ruoyi.quartz.entity.BaseJob;
import com.ruoyi.quartz.entity.BaseJobLog;
import com.ruoyi.quartz.entity.IJob;
import com.ruoyi.quartz.service.JobLogService;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;

/**
 * 抽象quartz调用
 *
 * @author ruoyi
 */
public abstract class AbstractQuartzJob implements Job
{
    private static final Logger log = LoggerFactory.getLogger(AbstractQuartzJob.class);


    protected JobLogService getJobService() {
        try {
            JobLogService service = SpringUtils.getBean(JobLogService.class);
            return service;
        }catch (Exception e) {
            return null;
        }
    }

    @Override
    public void execute(JobExecutionContext context)
    {
        IJob sysJob = new BaseJob();
        BeanUtils.copyBeanProp(sysJob, context.getMergedJobDataMap().get(ScheduleConstants.TASK_PROPERTIES));
        Date startTime = new Date();
        try
        {
            before(context, sysJob);
            if (sysJob != null)
            {
                doExecute(context, sysJob);
            }
            after(context, sysJob, startTime, null);
        }
        catch (Exception e)
        {
            log.error("任务执行异常  - ：", e);
            after(context, sysJob, startTime, e);
        }
    }

    /**
     * 执行前
     *
     * @param context 工作执行上下文对象
     * @param sysJob 系统计划任务
     */
    protected void before(JobExecutionContext context, IJob sysJob)
    {

    }

    /**
     * 执行后
     *
     * @param context 工作执行上下文对象
     * @param sysJob 系统计划任务
     */
    protected BaseJobLog after(JobExecutionContext context, IJob sysJob, Date startTime, Exception e)
    {
        final BaseJobLog sysJobLog = new BaseJobLog();
        sysJobLog.setJobName(sysJob.getJobName());
        sysJobLog.setJobGroup(sysJob.getJobGroup());
        sysJobLog.setInvokeTarget(sysJob.getInvokeTarget());
        sysJobLog.setStartTime(startTime);
        sysJobLog.setStopTime(new Date());
        long runMs = sysJobLog.getStopTime().getTime() - sysJobLog.getStartTime().getTime();
        sysJobLog.setJobMessage(sysJobLog.getJobName() + " 总共耗时：" + runMs + "毫秒");
        if (e != null)
        {
            sysJobLog.setStatus("1");
            String errorMsg = StringUtils.substring(ExceptionUtil.getExceptionMessage(e), 0, 2000);
            sysJobLog.setExceptionInfo(errorMsg);
        }
        else
        {
            sysJobLog.setStatus("0");
        }
        log.info("执行定时任务 日志信息 {}", JSON.toJSON(sysJobLog));

        // 写入数据库当中
        JobLogService jobService = getJobService();
        if(jobService == null) {
            log.warn("未找到写入日志服务");
        }else {
            jobService.addJobLog(sysJobLog);
        }
        return sysJobLog;
    }


    /**
     * 执行方法，由子类重载
     *
     * @param context 工作执行上下文对象
     * @param sysJob 系统计划任务
     * @throws Exception 执行过程中的异常
     */
    protected abstract void doExecute(JobExecutionContext context, IJob sysJob) throws Exception;
}
