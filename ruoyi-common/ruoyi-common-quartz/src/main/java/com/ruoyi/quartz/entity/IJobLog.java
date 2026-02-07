package com.ruoyi.quartz.entity;

import java.util.Date;

public interface IJobLog {

    public String getJobName();

    /** 任务组名 */
    public String getJobGroup();

    /** 调用目标字符串 */
    public String getInvokeTarget();

    /** 日志信息 */
    public String getJobMessage();

    /** 执行状态（0正常 1失败） */
    public String getStatus();

    /** 异常信息 */
    public String getExceptionInfo();

    /** 开始时间 */
    public Date getStartTime();

    /** 停止时间 */
    public Date getStopTime();
}
