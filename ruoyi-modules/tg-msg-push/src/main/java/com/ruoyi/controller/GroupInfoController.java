package com.ruoyi.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.ruoyi.common.core.domain.R;
import com.ruoyi.common.core.utils.StringUtils;
import com.ruoyi.common.domain.SysRole;
import com.ruoyi.common.entity.PageData;
import com.ruoyi.common.mp.service.IBaseService;
import com.ruoyi.common.security.auth.AuthUtil;
import com.ruoyi.common.security.utils.SecurityUtils;
import com.ruoyi.db.domain.GroupInfo;
import com.ruoyi.db.service.IGroupInfoService;
import com.ruoyi.db.service.IMsgInfoService;
import com.ruoyi.web.base.BaseController;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.PatternMatchUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;

/**
 * 网站信息Controller
 * 
 * @author ruoyi
 * @date 2025-08-07
 */
@Api(tags = "网站信息")
@RestController
@RequestMapping("/groupInfo")
public class GroupInfoController extends BaseController<GroupInfo>
{
    @Autowired
    private IGroupInfoService groupInfoService;

    @Override
    public String getModule() {
        return "tgPush:groupInfo";
    }

    @Override
    public String getModuleName() {
        return "群组信息";
    }

    @Override
    public IBaseService<GroupInfo> getService() {
        return groupInfoService;
    }

    @Override
    protected boolean ignoreScope() {
        return false;
    }

    @Override
    protected R<PageData<GroupInfo>> listData(GroupInfo queryData) {
        QueryWrapper<GroupInfo> queryWrapper = getQueryWrapper(queryData);

        Set<String> roles = SecurityUtils.getLoginUser().getRoles();
        boolean hasRole = roles.stream().filter(StringUtils::hasText)
                .anyMatch(x -> PatternMatchUtils.simpleMatch(x, "tg_push"));
        if(hasRole) {
            queryWrapper.lambda().inSql(GroupInfo::getTgId, "select tg_id from tg_info where owner_uid = " + SecurityUtils.getUserId());
        }
        PageData<GroupInfo> pageData = getService().selectPage(queryData.getPageInfo(), queryWrapper);
        return R.ok(pageData);
    }

}
