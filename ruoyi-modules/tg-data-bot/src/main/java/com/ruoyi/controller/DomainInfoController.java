package com.ruoyi.controller;

import com.ruoyi.common.mp.service.IBaseService;
import com.ruoyi.db.domain.DomainInfo;
import com.ruoyi.db.service.IDomainInfoService;
import com.ruoyi.web.base.BaseController;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 网站信息Controller
 * 
 * @author ruoyi
 * @date 2025-08-07
 */
@Api(tags = "网站信息")
@RestController
@RequestMapping("/domainInfo")
public class DomainInfoController extends BaseController<DomainInfo>
{
    @Autowired
    private IDomainInfoService domainInfoService;

    @Override
    public String getModule() {
        return "tg:domainInfo";
    }

    @Override
    public String getModuleName() {
        return "网站信息";
    }

    @Override
    public IBaseService<DomainInfo> getService() {
        return domainInfoService;
    }

}
