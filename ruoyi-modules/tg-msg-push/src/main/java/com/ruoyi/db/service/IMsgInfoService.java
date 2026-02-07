package com.ruoyi.db.service;

import com.ruoyi.common.mp.service.IBaseService;
import com.ruoyi.db.domain.MsgInfo;
import org.quartz.SchedulerException;
import org.springframework.transaction.annotation.Transactional;

/**
 * 充值订单Service接口
 * 
 * @author ruoyi
 * @date 2025-06-09
 */
public interface IMsgInfoService extends IBaseService<MsgInfo>
{

    @Transactional(rollbackFor = Exception.class)
    boolean pauseJob(MsgInfo job) throws SchedulerException;

    @Transactional(rollbackFor = Exception.class)
    boolean resumeJob(MsgInfo job) throws SchedulerException;

    @Transactional(rollbackFor = Exception.class)
    boolean changeStatus(MsgInfo job) throws SchedulerException;

    @Transactional(rollbackFor = Exception.class)
    boolean run(MsgInfo job) throws SchedulerException;

}
