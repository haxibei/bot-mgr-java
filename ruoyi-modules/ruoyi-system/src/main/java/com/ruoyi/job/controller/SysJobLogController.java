package com.ruoyi.job.controller;

import com.ruoyi.common.core.domain.R;
import com.ruoyi.common.core.utils.poi.ExcelUtil;
import com.ruoyi.common.log.annotation.Log;
import com.ruoyi.common.log.enums.BusinessType;
import com.ruoyi.common.mp.service.IBaseService;
import com.ruoyi.common.security.annotation.RequiresPermissions;
import com.ruoyi.job.domain.SysJobLog;
import com.ruoyi.job.service.ISysJobLogService;
import com.ruoyi.web.base.BaseController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 调度日志操作处理
 * 
 * @author ruoyi
 */
@RestController
@RequestMapping("/job/log")
public class SysJobLogController extends BaseController<SysJobLog>
{
    @Autowired
    private ISysJobLogService jobLogService;

    /**
     * 导出定时任务调度日志列表
     */
    @RequiresPermissions("monitor:job:export")
    @Log(title = "任务调度日志", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, SysJobLog sysJobLog)
    {
        List<SysJobLog> list = jobLogService.selectJobLogList(sysJobLog);
        ExcelUtil<SysJobLog> util = new ExcelUtil<SysJobLog>(SysJobLog.class);
        util.exportExcel(response, list, "调度日志");
    }

    /**
     * 清空定时任务调度日志
     */
    @RequiresPermissions("monitor:job:remove")
    @Log(title = "调度日志", businessType = BusinessType.CLEAN)
    @DeleteMapping("/clean")
    public R clean()
    {
        jobLogService.cleanJobLog();
        return R.ok();
    }

    @Override
    public String getModule() {
        return "monitor:job";
    }

    @Override
    public String getModuleName() {
        return "任务调度日志";
    }

    @Override
    public IBaseService getService() {
        return jobLogService;
    }
}
