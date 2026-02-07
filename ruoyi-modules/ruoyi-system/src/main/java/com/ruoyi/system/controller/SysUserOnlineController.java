package com.ruoyi.system.controller;

import com.ruoyi.common.core.constant.CacheConstants;
import com.ruoyi.common.core.domain.R;
import com.ruoyi.common.core.utils.StringUtils;
import com.ruoyi.common.entity.LoginUser;
import com.ruoyi.common.log.annotation.Log;
import com.ruoyi.common.log.enums.BusinessType;
import com.ruoyi.common.redis.constant.DefaultJedisKeyNS;
import com.ruoyi.common.redis.service.RedisService;
import com.ruoyi.common.security.annotation.RequiresPermissions;
import com.ruoyi.system.domain.SysUserOnline;
import com.ruoyi.system.service.ISysUserOnlineService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * 在线用户监控
 * 
 * @author ruoyi
 */
@RestController
@RequestMapping("/online")
public class SysUserOnlineController
{
    @Autowired
    private ISysUserOnlineService userOnlineService;

    @Autowired
    private RedisService redisService;

    @RequiresPermissions("monitor:online:list")
    @GetMapping("/list")
    public R<List<SysUserOnline>> list(String ipaddr, String userName)
    {
        Collection<String> keys = redisService.keys(DefaultJedisKeyNS.token + ":" + CacheConstants.LOGIN_TOKEN_KEY + "*");
        List<SysUserOnline> userOnlineList = new ArrayList<SysUserOnline>();
        for (String key : keys)
        {
            LoginUser user = redisService.getCacheObject(DefaultJedisKeyNS.token, key);
            if (StringUtils.isNotEmpty(ipaddr) && StringUtils.isNotEmpty(userName))
            {
                userOnlineList.add(userOnlineService.selectOnlineByInfo(ipaddr, userName, user));
            }
            else if (StringUtils.isNotEmpty(ipaddr))
            {
                userOnlineList.add(userOnlineService.selectOnlineByIpaddr(ipaddr, user));
            }
            else if (StringUtils.isNotEmpty(userName))
            {
                userOnlineList.add(userOnlineService.selectOnlineByUserName(userName, user));
            }
            else
            {
                userOnlineList.add(userOnlineService.loginUserToUserOnline(user));
            }
        }
        Collections.reverse(userOnlineList);
        userOnlineList.removeAll(Collections.singleton(null));
        return R.ok(userOnlineList);
    }

    /**
     * 强退用户
     */
    @RequiresPermissions("monitor:online:forceLogout")
    @Log(title = "在线用户", businessType = BusinessType.FORCE)
    @DeleteMapping("/{tokenId}")
    public R forceLogout(@PathVariable String tokenId)
    {
        redisService.deleteObject(DefaultJedisKeyNS.token, CacheConstants.LOGIN_TOKEN_KEY + tokenId);
        return R.ok();
    }
}
