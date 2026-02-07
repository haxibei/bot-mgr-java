package com.ruoyi.system.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.ruoyi.common.core.domain.R;
import com.ruoyi.common.core.utils.StringUtils;
import com.ruoyi.common.domain.SysRole;
import com.ruoyi.common.mp.service.IBaseService;
import com.ruoyi.system.domain.SysTenant;
import com.ruoyi.system.service.ISysTenantService;
import com.ruoyi.web.base.BaseController;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 租户信息Controller
 * 
 * @author ruoyi
 * @date 2025-04-20
 */
@Api(tags = "租户信息")
@RestController
@RequestMapping("/tenant")
public class SysTenantController extends BaseController<SysTenant>
{
    @Autowired
    private ISysTenantService sysTenantService;

    @Override
    public String getModule() {
        return "sysTenant";
    }

    @Override
    public String getModuleName() {
        return "租户信息";
    }

    @Override
    public IBaseService<SysTenant> getService() {
        return sysTenantService;
    }

    @GetMapping("listAllTenant")
    public R<List<SysTenant>> listAll()
    {
        SysTenant queryData = new SysTenant();

        QueryWrapper<SysTenant> baseWrapper = sysTenantService.getBaseWrapper(queryData);

        baseWrapper.lambda().select(SysTenant::getTenantId,SysTenant::getTenantName);
        List<SysTenant> list = sysTenantService.list(baseWrapper);
        return R.ok(list);
    }
}
