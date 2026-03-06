package com.ruoyi.controller;

import com.ruoyi.common.mp.service.IBaseService;
import com.ruoyi.db.domain.AgentBindInfo;
import com.ruoyi.db.service.IAgentBindInfoService;
import com.ruoyi.web.base.BaseController;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 代理绑定信息Controller
 * 
 * @author ruoyi
 * @date 2026-02-21
 */
@Api(tags = "代理绑定信息")
@RestController
@RequestMapping("/agentBindInfo")
public class AgentBindInfoController extends BaseController<AgentBindInfo>
{
    @Autowired
    private IAgentBindInfoService agentBindInfoService;

    @Override
    public String getModule() {
        return "tg:agentBindInfo";
    }

    @Override
    public String getModuleName() {
        return "代理绑定信息";
    }

    @Override
    public IBaseService<AgentBindInfo> getService() {
        return agentBindInfoService;
    }

}
