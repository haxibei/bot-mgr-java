package com.ruoyi.db.service.impl;

import com.ruoyi.common.core.constant.ScheduleConstants;
import com.ruoyi.common.core.exception.job.TaskException;
import com.ruoyi.common.core.utils.StringUtils;
import com.ruoyi.common.mp.service.impl.BaseServiceImpl;
import com.ruoyi.db.domain.MsgInfo;
import com.ruoyi.db.mapper.MsgInfoMapper;
import com.ruoyi.db.service.IMsgInfoService;
import com.ruoyi.quartz.util.CronUtils;
import com.ruoyi.quartz.util.ScheduleUtils;
import org.quartz.JobDataMap;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.io.Serializable;
import java.util.Collection;
import java.util.List;

/**
 * @author ruoyi
 * @date 2025-06-09
 */
@Service
public class MsgInfoServiceImpl extends BaseServiceImpl<MsgInfo, MsgInfoMapper> implements IMsgInfoService
{

    @Autowired
    private Scheduler scheduler;

    /**
     * 项目启动时，初始化定时器 主要是防止手动修改数据库导致未同步到定时任务处理（注：不能手动修改数据库ID和任务组名，否则会导致脏数据）
     */
    @PostConstruct
    public void init() throws SchedulerException, TaskException
    {
        scheduler.clear();
        List<MsgInfo> jobList = this.getBaseMapper().selectJobAll();
        for (MsgInfo job : jobList)
        {
            ScheduleUtils.createScheduleJob(scheduler, job);
        }
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean pauseJob(MsgInfo job) throws SchedulerException
    {
        Long jobId = job.getJobId();
        String jobGroup = job.getJobGroup();
        job.setStatus(ScheduleConstants.Status.PAUSE.getValue());
        boolean ret = this.updateById(job);
        if (ret)
        {
            scheduler.pauseJob(ScheduleUtils.getJobKey(jobId, jobGroup));
        }
        return ret;
    }

    /**
     * 恢复任务
     *
     * @param job 调度信息
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean resumeJob(MsgInfo job) throws SchedulerException
    {
        Long jobId = job.getJobId();
        String jobGroup = job.getJobGroup();
        job.setStatus(ScheduleConstants.Status.NORMAL.getValue());
        boolean ret = this.updateById(job);
        if (ret)
        {
            scheduler.resumeJob(ScheduleUtils.getJobKey(jobId, jobGroup));
        }
        return ret;
    }

    /**
     * 删除任务后，所对应的trigger也将被删除
     *
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean removeByIds(Collection<?> msgIds)
    {
        List<MsgInfo> msgInfos = this.listByIds((Collection<? extends Serializable>) msgIds);
        boolean ret = super.removeByIds(msgIds);
        if (ret)
        {
            for (MsgInfo job : msgInfos)
            {
                try {
                    scheduler.deleteJob(ScheduleUtils.getJobKey(job.getJobId(), job.getJobGroup()));
                } catch (SchedulerException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        return ret;
    }

    /**
     * 任务调度状态修改
     *
     * @param job 调度信息
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean changeStatus(MsgInfo job) throws SchedulerException
    {
        boolean ret = false;
        String status = job.getStatus();
        if (ScheduleConstants.Status.NORMAL.getValue().equals(status))
        {
            ret = resumeJob(job);
        }
        else if (ScheduleConstants.Status.PAUSE.getValue().equals(status))
        {
            ret = pauseJob(job);
        }
        return ret;
    }

    /**
     * 立即运行任务
     *
     * @param job 调度信息
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean run(MsgInfo job) throws SchedulerException
    {
        boolean result = false;
        Long jobId = job.getJobId();
        String jobGroup = job.getJobGroup();
        MsgInfo properties = getById(job.getJobId());
        // 参数
        JobDataMap dataMap = new JobDataMap();
        dataMap.put(ScheduleConstants.TASK_PROPERTIES, properties);
        JobKey jobKey = ScheduleUtils.getJobKey(jobId, jobGroup);
        if (scheduler.checkExists(jobKey))
        {
            result = true;
            scheduler.triggerJob(jobKey, dataMap);
        }
        return result;
    }

    /**
     * 新增任务
     *
     * @param job 调度信息 调度信息
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean save(MsgInfo job) {
        job.setStatus(StringUtils.isNotBlank(job.getStatus())?job.getStatus():ScheduleConstants.Status.PAUSE.getValue());
        boolean ret = super.save(job);
        if (ret)
        {
            try {
                ScheduleUtils.createScheduleJob(scheduler, job);
            } catch (SchedulerException | TaskException e) {
                throw new RuntimeException(e);
            }
        }
        return ret;
    }

    /**
     * 更新任务的时间表达式
     *
     * @param job 调度信息
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateById(MsgInfo job)
    {
        MsgInfo properties = getById(job.getJobId());
        boolean ret = super.updateById(job);
        if (ret)
        {
            try {
                updateSchedulerJob(job, properties.getJobGroup());
            } catch (SchedulerException | TaskException e) {
                throw new RuntimeException(e);
            }
        }
        return ret;
    }

    /**
     * 更新任务
     *
     * @param job 任务对象
     * @param jobGroup 任务组名
     */
    public void updateSchedulerJob(MsgInfo job, String jobGroup) throws SchedulerException, TaskException
    {
        Long jobId = job.getJobId();
        // 判断是否存在
        JobKey jobKey = ScheduleUtils.getJobKey(jobId, jobGroup);
        if (scheduler.checkExists(jobKey))
        {
            // 防止创建时存在数据问题 先移除，然后在执行创建操作
            scheduler.deleteJob(jobKey);
        }
        ScheduleUtils.createScheduleJob(scheduler, job);
    }

}
