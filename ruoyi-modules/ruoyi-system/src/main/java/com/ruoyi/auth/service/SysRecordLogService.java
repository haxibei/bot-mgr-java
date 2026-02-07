package com.ruoyi.auth.service;

import com.ruoyi.common.core.constant.Constants;
import com.ruoyi.common.core.constant.SecurityConstants;
import com.ruoyi.common.core.utils.StringUtils;
import com.ruoyi.common.core.utils.ip.IpUtils;
import com.ruoyi.common.domain.SysLogininfor;
import com.ruoyi.system.api.RemoteLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * 记录日志方法
 * 
 * @author ruoyi
 */
@Component
public class SysRecordLogService
{
    @Autowired
    private RemoteLogService remoteLogService;

    /**
     * 记录登录信息
     * 
     * @param username 用户名
     * @param status 状态
     * @param message 消息内容
     * @return
     */
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
        remoteLogService.saveLogininfor(logininfor, SecurityConstants.INNER);
    }
}
