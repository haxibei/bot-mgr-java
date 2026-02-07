package com.ruoyi.system.mapper;

import com.ruoyi.common.domain.SysOperLog;
import com.ruoyi.common.mp.dao.IBaseDao;

import java.util.List;

/**
 * 操作日志 数据层
 * 
 * @author ruoyi
 */
public interface SysOperLogMapper extends IBaseDao<SysOperLog>
{
    /**
     * 清空操作日志
     */
    void cleanOperLog();
}

