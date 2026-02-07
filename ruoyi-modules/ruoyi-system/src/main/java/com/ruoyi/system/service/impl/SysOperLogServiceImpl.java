package com.ruoyi.system.service.impl;

import java.util.List;

import com.ruoyi.common.domain.SysOperLog;
import com.ruoyi.common.mp.service.impl.BaseServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.system.mapper.SysOperLogMapper;
import com.ruoyi.system.service.ISysOperLogService;

/**
 * 操作日志 服务层处理
 * 
 * @author ruoyi
 */
@Service
public class SysOperLogServiceImpl extends BaseServiceImpl<SysOperLog, SysOperLogMapper> implements ISysOperLogService
{

    /**
     * 清空操作日志
     */
    @Override
    public void cleanOperLog()
    {
        this.getBaseMapper().cleanOperLog();
    }
}
