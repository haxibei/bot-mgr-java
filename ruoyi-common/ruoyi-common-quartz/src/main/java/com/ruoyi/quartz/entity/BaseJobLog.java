package com.ruoyi.quartz.entity;

import com.ruoyi.common.core.annotation.Excel;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class BaseJobLog implements IJobLog {

    private String jobName;

    /** 任务组名 */
    private String jobGroup;

    /** 调用目标字符串 */
    private String invokeTarget;

    /** 日志信息 */
    private String jobMessage;

    /** 执行状态（0正常 1失败） */
    private String status;

    /** 异常信息 */
    private String exceptionInfo;

    /** 开始时间 */
    private Date startTime;

    /** 停止时间 */
    private Date stopTime;
}
