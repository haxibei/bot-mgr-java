package com.ruoyi.system.controller;

import com.ruoyi.common.mp.service.IBaseService;
import com.ruoyi.system.domain.SysNotice;
import com.ruoyi.system.service.ISysNoticeService;
import com.ruoyi.web.base.BaseController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 公告 信息操作处理
 * 
 * @author ruoyi
 */
@RestController
@RequestMapping("/notice")
public class SysNoticeController extends BaseController<SysNotice>
{
    @Autowired
    private ISysNoticeService noticeService;

    @Override
    public String getModule() {
        return "system:notice";
    }

    @Override
    public String getModuleName() {
        return "通知公告";
    }

    @Override
    public IBaseService<SysNotice> getService() {
        return noticeService;
    }
}
