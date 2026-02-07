package com.ruoyi.system.service;

import com.ruoyi.common.domain.SysLogininfor;
import com.ruoyi.common.mp.service.IBaseService;

import java.util.List;

/**
 * 系统访问日志情况信息 服务层
 * 
 * @author ruoyi
 */
public interface ISysLogininforService extends IBaseService<SysLogininfor>
{
    /**
     * 清空系统登录日志
     */
    void cleanLogininfor();

    void recordLogininfor(String username, String loginFail, String s);
}
