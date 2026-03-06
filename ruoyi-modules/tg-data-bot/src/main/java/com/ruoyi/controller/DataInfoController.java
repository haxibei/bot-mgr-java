package com.ruoyi.controller;

import com.ruoyi.common.mp.service.IBaseService;
import com.ruoyi.db.domain.DataInfo;
import com.ruoyi.db.service.IDataInfoService;
import com.ruoyi.web.base.BaseController;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 每日信息Controller
 * 
 * @author ruoyi
 * @date 2026-02-21
 */
@Api(tags = "每日信息")
@RestController
@RequestMapping("/dataInfo")
public class DataInfoController extends BaseController<DataInfo>
{
    @Autowired
    private IDataInfoService dataInfoService;

    @Override
    public String getModule() {
        return "tg:dataInfo";
    }

    @Override
    public String getModuleName() {
        return "每日信息";
    }

    @Override
    public IBaseService<DataInfo> getService() {
        return dataInfoService;
    }

}
