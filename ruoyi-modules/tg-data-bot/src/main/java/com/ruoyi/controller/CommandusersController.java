package com.ruoyi.controller;

import com.ruoyi.common.mp.service.IBaseService;
import com.ruoyi.db.domain.Commandusers;
import com.ruoyi.db.service.ICommandusersService;
import com.ruoyi.web.base.BaseController;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 用户Controller
 * 
 * @author ruoyi
 * @date 2026-02-21
 */
@Api(tags = "用户")
@RestController
@RequestMapping("/commandusers")
public class CommandusersController extends BaseController<Commandusers>
{
    @Autowired
    private ICommandusersService commandusersService;

    @Override
    public String getModule() {
        return "tg:commandusers";
    }

    @Override
    public String getModuleName() {
        return "用户";
    }

    @Override
    public IBaseService<Commandusers> getService() {
        return commandusersService;
    }

}
