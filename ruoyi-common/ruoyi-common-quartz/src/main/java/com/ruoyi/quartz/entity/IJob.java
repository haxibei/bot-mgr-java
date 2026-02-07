package com.ruoyi.quartz.entity;
public interface IJob {

    public Long getJobId();

    /** 任务名称 */
    public String getJobName();

    /** 任务组名 */
    public String getJobGroup();

    /** 调用目标字符串 */
    public String getInvokeTarget();

    /** cron执行表达式 */
    public String getCronExpression();

    /** cron计划策略 */
    public String getMisfirePolicy();

    /** 是否并发执行（0允许 1禁止） */
    public String getConcurrent();

    /** 任务状态（0正常 1暂停） */
    public String getStatus();
}
