package com.ruoyi.controller;

import com.ruoyi.common.core.annotation.DataScope;
import com.ruoyi.common.core.domain.R;
import com.ruoyi.common.core.utils.StringUtils;
import com.ruoyi.common.mp.service.IBaseService;
import com.ruoyi.common.security.utils.SecurityUtils;
import com.ruoyi.db.domain.GroupInfo;
import com.ruoyi.db.domain.TgInfo;
import com.ruoyi.db.service.IGroupInfoService;
import com.ruoyi.db.service.ITgInfoService;
import com.ruoyi.web.base.BaseController;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.PatternMatchUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Set;

/**
 * 网站信息Controller
 * 
 * @author ruoyi
 * @date 2025-08-07
 */
@Api(tags = "个号信息")
@RestController
@RequestMapping("/tgInfo")
public class TgInfoController extends BaseController<TgInfo>
{
    @Autowired
    private ITgInfoService tgInfoService;

    @Override
    public String getModule() {
        return "tgPush:tgInfo";
    }

    @Override
    public String getModuleName() {
        return "个号信息";
    }

    @Override
    public IBaseService<TgInfo> getService() {
        return tgInfoService;
    }

    @Override
    protected boolean ignoreScope() {
        return false;
    }

    @GetMapping("/getOwnTg")
    @DataScope
    public R<List<TgInfo>> getOwnTg(TgInfo query) {

        Set<String> roles = SecurityUtils.getLoginUser().getRoles();
        boolean hasRole = roles.stream().filter(StringUtils::hasText)
                .anyMatch(x -> PatternMatchUtils.simpleMatch(x, "tg_push"));
        if(hasRole) {
            query.setOwnerUid(SecurityUtils.getUserId());
        }

        List<TgInfo> groupInfos = tgInfoService.selectList(query);
        return R.ok(groupInfos);
    }
}
