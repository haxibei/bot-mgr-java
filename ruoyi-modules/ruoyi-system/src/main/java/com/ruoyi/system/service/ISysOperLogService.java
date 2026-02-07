package com.ruoyi.system.service;

import com.ruoyi.common.domain.SysOperLog;
import com.ruoyi.common.mp.service.IBaseService;

/**
 * 操作日志 服务层
 * 
 * @author ruoyi
 */
public interface ISysOperLogService extends IBaseService<SysOperLog>
{
    /**
     * 清空操作日志
     */
    void cleanOperLog();
}
