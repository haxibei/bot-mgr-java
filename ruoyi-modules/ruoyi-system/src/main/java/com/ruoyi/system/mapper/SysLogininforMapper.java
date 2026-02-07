package com.ruoyi.system.mapper;

import com.ruoyi.common.domain.SysLogininfor;
import com.ruoyi.common.mp.dao.IBaseDao;

import java.util.List;

/**
 * 系统访问日志情况信息 数据层
 * 
 * @author ruoyi
 */
public interface SysLogininforMapper extends IBaseDao<SysLogininfor>
{

    /**
     * 清空系统登录日志
     *
     * @return 结果
     */
    int cleanLogininfor();
}
