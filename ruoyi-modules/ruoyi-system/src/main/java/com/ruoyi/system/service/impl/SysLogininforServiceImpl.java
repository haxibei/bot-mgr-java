package com.ruoyi.system.service.impl;

import java.util.Date;
import java.util.List;

import com.ruoyi.common.core.constant.Constants;
import com.ruoyi.common.core.utils.StringUtils;
import com.ruoyi.common.core.utils.ip.IpUtils;
import com.ruoyi.common.domain.SysLogininfor;
import com.ruoyi.common.mp.service.impl.BaseServiceImpl;
import org.springframework.stereotype.Service;
import com.ruoyi.system.mapper.SysLogininforMapper;
import com.ruoyi.system.service.ISysLogininforService;

/**
 * 系统访问日志情况信息 服务层处理
 * 
 * @author ruoyi
 */
@Service
public class SysLogininforServiceImpl extends BaseServiceImpl<SysLogininfor, SysLogininforMapper> implements ISysLogininforService
{

    /**
     * 清空系统登录日志
     */
    @Override
    public void cleanLogininfor()
    {
        this.getBaseMapper().cleanLogininfor();
    }

    @Override
    public void recordLogininfor(String username, String status, String message)
    {
        SysLogininfor logininfor = new SysLogininfor();
        logininfor.setUserName(username);
        logininfor.setIpaddr(IpUtils.getIpAddr());
        logininfor.setMsg(message);
        logininfor.setAccessTime(new Date());
        // 日志状态
        if (StringUtils.equalsAny(status, Constants.LOGIN_SUCCESS, Constants.LOGOUT, Constants.REGISTER))
        {
            logininfor.setStatus(Constants.LOGIN_SUCCESS_STATUS);
        }
        else if (Constants.LOGIN_FAIL.equals(status))
        {
            logininfor.setStatus(Constants.LOGIN_FAIL_STATUS);
        }
        save(logininfor);
    }
}
