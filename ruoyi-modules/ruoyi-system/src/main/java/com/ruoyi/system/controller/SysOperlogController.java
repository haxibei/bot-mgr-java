package com.ruoyi.system.controller;

import com.ruoyi.common.core.domain.R;
import com.ruoyi.common.core.utils.poi.ExcelUtil;
import com.ruoyi.common.domain.SysOperLog;
import com.ruoyi.common.entity.PageData;
import com.ruoyi.common.log.annotation.Log;
import com.ruoyi.common.log.enums.BusinessType;
import com.ruoyi.common.security.annotation.InnerAuth;
import com.ruoyi.common.security.annotation.RequiresPermissions;
import com.ruoyi.system.service.ISysOperLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * 操作日志记录
 * 
 * @author ruoyi
 */
@RestController
@RequestMapping("/operlog")
public class SysOperlogController
{
    @Autowired
    private ISysOperLogService operLogService;

    @RequiresPermissions("system:operlog:list")
    @GetMapping("/list")
    public R<PageData<SysOperLog>> list(SysOperLog operLog)
    {
        PageData<SysOperLog> list = operLogService.selectPage(operLog);
        return R.ok(list);
    }

    @Log(title = "操作日志", businessType = BusinessType.EXPORT)
    @RequiresPermissions("system:operlog:export")
    @PostMapping("/export")
    public void export(HttpServletResponse response, SysOperLog operLog)
    {
        List<SysOperLog> list = operLogService.selectList(operLog);
        ExcelUtil<SysOperLog> util = new ExcelUtil<SysOperLog>(SysOperLog.class);
        util.exportExcel(response, list, "操作日志");
    }

    @Log(title = "操作日志", businessType = BusinessType.DELETE)
    @RequiresPermissions("system:operlog:remove")
    @DeleteMapping("/{operIds}")
    public R remove(@PathVariable Long[] operIds)
    {
        boolean ret = operLogService.removeByIds(Arrays.asList(operIds));
        return R.ok(ret?1:0);
    }

    @RequiresPermissions("system:operlog:remove")
    @Log(title = "操作日志", businessType = BusinessType.CLEAN)
    @DeleteMapping("/clean")
    public R clean()
    {
        operLogService.cleanOperLog();
        return R.ok();
    }

    @InnerAuth
    @PostMapping
    public R add(@RequestBody SysOperLog operLog)
    {
        operLog.setOperTime(new Date());
        return R.ok(operLogService.save(operLog));
    }
}
