package com.ruoyi.quartz.util;

import com.ruoyi.common.core.constant.Constants;
import com.ruoyi.common.core.constant.ScheduleConstants;
import com.ruoyi.common.core.exception.job.TaskException;
import com.ruoyi.common.core.exception.job.TaskException.Code;
import com.ruoyi.common.core.utils.SpringUtils;
import com.ruoyi.common.core.utils.StringUtils;
import com.ruoyi.quartz.entity.IJob;
import org.quartz.*;

import java.util.Date;
import java.util.regex.Pattern;
import java.util.Calendar;

/**
 * 定时任务工具类
 * 
 * @author ruoyi
 *
 */
public class ScheduleUtils
{
    /**
     * 得到quartz任务类
     *
     * @param sysJob 执行计划
     * @return 具体执行任务类
     */
    private static Class<? extends Job> getQuartzJobClass(IJob sysJob)
    {
        boolean isConcurrent = "0".equals(sysJob.getConcurrent());
        return isConcurrent ? QuartzJobExecution.class : QuartzDisallowConcurrentExecution.class;
    }

    /**
     * 构建任务触发对象
     */
    public static TriggerKey getTriggerKey(Long jobId, String jobGroup)
    {
        return TriggerKey.triggerKey(ScheduleConstants.TASK_CLASS_NAME + jobId, jobGroup);
    }

    /**
     * 构建任务键对象
     */
    public static JobKey getJobKey(Long jobId, String jobGroup)
    {
        return JobKey.jobKey(ScheduleConstants.TASK_CLASS_NAME + jobId, jobGroup);
    }

    /**
     * 创建定时任务
     */
    public static void createScheduleJob(Scheduler scheduler, IJob job) throws SchedulerException, TaskException
    {
        Class<? extends Job> jobClass = getQuartzJobClass(job);
        // 构建job信息
        Long jobId = job.getJobId();
        String jobGroup = job.getJobGroup();
        JobDetail jobDetail = JobBuilder.newJob(jobClass).withIdentity(getJobKey(jobId, jobGroup)).build();

        // 表达式调度构建器
        Trigger trigger = null;

        // 时间是 (每分钟多少 且 这个时间大于 30) 执行一次的 换一个执行方式
        String perMinute = matchPerMinute(job.getCronExpression());
        boolean needSourceJob = true;
        if(perMinute != null) {
            String[] arr = perMinute.split("/");
            int cycle = Integer.parseInt(arr[1]);
            if(cycle > 30) {
                int startMinute = Integer.parseInt(arr[0]);
                SimpleScheduleBuilder simpleScheduleBuilder = SimpleScheduleBuilder.simpleSchedule()
                        .withIntervalInMinutes(cycle)   // 每n分钟一次
                        .repeatForever();
                trigger = TriggerBuilder.newTrigger().withIdentity(getTriggerKey(jobId, jobGroup))
                        .startAt(getStartTime(startMinute))
                        .withSchedule(simpleScheduleBuilder).build();

                needSourceJob = false;
            }
        }
        if(needSourceJob) {
            CronScheduleBuilder cronScheduleBuilder = CronScheduleBuilder.cronSchedule(job.getCronExpression());
            cronScheduleBuilder = handleCronScheduleMisfirePolicy(job, cronScheduleBuilder);
            // 按新的cronExpression表达式构建一个新的trigger
            trigger = TriggerBuilder.newTrigger().withIdentity(getTriggerKey(jobId, jobGroup))
                    .withSchedule(cronScheduleBuilder).build();
        }

        // 放入参数，运行时的方法可以获取
        jobDetail.getJobDataMap().put(ScheduleConstants.TASK_PROPERTIES, job);

        // 判断是否存在
        if (scheduler.checkExists(getJobKey(jobId, jobGroup)))
        {
            // 防止创建时存在数据问题 先移除，然后在执行创建操作
            scheduler.deleteJob(getJobKey(jobId, jobGroup));
        }

        // 判断任务是否过期
        if (StringUtils.isNotNull(CronUtils.getNextExecution(job.getCronExpression())))
        {
            // 执行调度任务
            scheduler.scheduleJob(jobDetail, trigger);
        }

        // 暂停任务
        if (job.getStatus().equals(ScheduleConstants.Status.PAUSE.getValue()))
        {
            scheduler.pauseJob(ScheduleUtils.getJobKey(jobId, jobGroup));
        }
    }

    private static String regex = "^0\\s+\\d+/\\d+\\s+\\*\\s+\\*\\s+\\*\\s+\\?$";
    public static String matchPerMinute(String cron) {
        boolean match = Pattern.matches(regex, cron);
        if(match) {
            return cron.split(" ")[1];
        }
        return null;
    }

    private static Date getStartTime(int startMinute) {
        Calendar cal = Calendar.getInstance(); // 当前时间
        int currentMinute = cal.get(Calendar.MINUTE);

        if (startMinute > currentMinute) {
            // 当小时的对应分钟
            cal.set(Calendar.MINUTE, startMinute);
        } else {
            // 下一小时的对应分钟
            cal.add(Calendar.HOUR_OF_DAY, 1);
            cal.set(Calendar.MINUTE, startMinute);
        }
        // 秒、毫秒清零以更精准
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();
    }

    /**
     * 设置定时任务策略
     */
    public static CronScheduleBuilder handleCronScheduleMisfirePolicy(IJob job, CronScheduleBuilder cb)
            throws TaskException
    {
        switch (job.getMisfirePolicy())
        {
            case ScheduleConstants.MISFIRE_DEFAULT:
                return cb;
            case ScheduleConstants.MISFIRE_IGNORE_MISFIRES:
                return cb.withMisfireHandlingInstructionIgnoreMisfires();
            case ScheduleConstants.MISFIRE_FIRE_AND_PROCEED:
                return cb.withMisfireHandlingInstructionFireAndProceed();
            case ScheduleConstants.MISFIRE_DO_NOTHING:
                return cb.withMisfireHandlingInstructionDoNothing();
            default:
                throw new TaskException("The task misfire policy '" + job.getMisfirePolicy()
                        + "' cannot be used in cron schedule tasks", Code.CONFIG_ERROR);
        }
    }

    /**
     * 检查包名是否为白名单配置
     * 
     * @param invokeTarget 目标字符串
     * @return 结果
     */
    public static boolean whiteList(String invokeTarget)
    {
        String packageName = StringUtils.substringBefore(invokeTarget, "(");
        int count = StringUtils.countMatches(packageName, ".");
        if (count > 1)
        {
            return StringUtils.containsAnyIgnoreCase(invokeTarget, Constants.JOB_WHITELIST_STR);
        }
        Object obj = SpringUtils.getBean(StringUtils.split(invokeTarget, ".")[0]);
        String beanPackageName = obj.getClass().getPackage().getName();
        return StringUtils.containsAnyIgnoreCase(beanPackageName, Constants.JOB_WHITELIST_STR)
                && !StringUtils.containsAnyIgnoreCase(beanPackageName, Constants.JOB_ERROR_STR);
    }
}