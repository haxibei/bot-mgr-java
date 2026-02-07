package com.ruoyi.system.controller;

import com.ruoyi.common.core.constant.CacheConstants;
import com.ruoyi.common.core.domain.R;
import com.ruoyi.common.core.utils.poi.ExcelUtil;
import com.ruoyi.common.domain.SysLogininfor;
import com.ruoyi.common.entity.PageData;
import com.ruoyi.common.log.annotation.Log;
import com.ruoyi.common.log.enums.BusinessType;
import com.ruoyi.common.redis.constant.DefaultJedisKeyNS;
import com.ruoyi.common.redis.service.RedisService;
import com.ruoyi.common.security.annotation.InnerAuth;
import com.ruoyi.common.security.annotation.RequiresPermissions;
import com.ruoyi.system.service.ISysLogininforService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.List;

/**
 * 系统访问记录
 * 
 * @author ruoyi
 */
@RestController
@RequestMapping("/logininfor")
public class SysLogininforController
{
    @Autowired
    private ISysLogininforService logininforService;

    @Autowired
    private RedisService redisService;

    @RequiresPermissions("system:logininfor:list")
    @GetMapping("/list")
    public R<PageData<SysLogininfor>> list(SysLogininfor logininfor)
    {
        PageData<SysLogininfor> list = logininforService.selectPage(logininfor);
        return R.ok(list);
    }

    @Log(title = "登录日志", businessType = BusinessType.EXPORT)
    @RequiresPermissions("system:logininfor:export")
    @PostMapping("/export")
    public void export(HttpServletResponse response, SysLogininfor logininfor)
    {
        List<SysLogininfor> list = logininforService.selectList(logininfor);
        ExcelUtil<SysLogininfor> util = new ExcelUtil<SysLogininfor>(SysLogininfor.class);
        util.exportExcel(response, list, "登录日志");
    }

    @RequiresPermissions("system:logininfor:remove")
    @Log(title = "登录日志", businessType = BusinessType.DELETE)
    @DeleteMapping("/{infoIds}")
    public R remove(@PathVariable Long[] infoIds)
    {
        boolean ret = logininforService.removeByIds(Arrays.asList(infoIds));
        return R.ok(ret?1:0);
    }

    @RequiresPermissions("system:logininfor:remove")
    @Log(title = "登录日志", businessType = BusinessType.DELETE)
    @DeleteMapping("/clean")
    public R clean()
    {
        logininforService.cleanLogininfor();
        return R.ok();
    }

    @RequiresPermissions("system:logininfor:unlock")
    @Log(title = "账户解锁", businessType = BusinessType.OTHER)
    @GetMapping("/unlock/{userName}")
    public R unlock(@PathVariable("userName") String userName)
    {
        redisService.deleteObject(DefaultJedisKeyNS.auth, CacheConstants.PWD_ERR_CNT_KEY + userName);
        return R.ok();
    }

    @InnerAuth
    @PostMapping
    public R add(@RequestBody SysLogininfor logininfor)
    {
        return R.ok(logininforService.save(logininfor));
    }
}
