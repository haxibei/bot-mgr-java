package com.ruoyi.common.datascope.aspect;

import com.ruoyi.common.core.annotation.DataScope;
import com.ruoyi.common.core.constant.SecurityConstants;
import com.ruoyi.common.core.context.SecurityContextHolder;
import com.ruoyi.common.core.utils.StringUtils;
import com.ruoyi.common.core.web.domain.IScopeEntity;
import com.ruoyi.common.datascope.utils.ScopeService;
import com.ruoyi.common.domain.SysUser;
import com.ruoyi.common.entity.LoginUser;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * 数据过滤处理
 * 
 * @author ruoyi
 */
@Aspect
@Component
@Order(2)
public class DataScopeAspect
{
    @Resource
    private ScopeService scopeService;

    @Before("@annotation(controllerDataScope)")
    public void doBefore(JoinPoint point, DataScope controllerDataScope) throws Throwable
    {
        clearDataScope(point);
        handleDataScope(point, controllerDataScope);
    }

    protected void handleDataScope(final JoinPoint joinPoint, DataScope controllerDataScope)
    {
        // 获取当前的用户
        LoginUser loginUser = SecurityContextHolder.get(SecurityConstants.LOGIN_USER, LoginUser.class);
        if (StringUtils.isNotNull(loginUser))
        {
            SysUser currentUser = loginUser.getSysUser();
            // 如果是超级管理员，则不过滤数据
            if (StringUtils.isNotNull(currentUser) && !currentUser.isAdmin())
            {
                dataScopeFilter(joinPoint, currentUser, controllerDataScope.deptAlias(),
                        controllerDataScope.userAlias());
            }
        }
    }

    /**
     * 数据范围过滤
     * 
     * @param joinPoint 切点
     * @param user 用户
     * @param deptAlias 部门别名
     * @param userAlias 用户别名
     */
    public void dataScopeFilter(JoinPoint joinPoint, SysUser user, String deptAlias, String userAlias)
    {
        StringBuilder sqlString = scopeService.getScopeSqlSeg(user, deptAlias, userAlias);

        Object params = joinPoint.getArgs()[0];
        if (StringUtils.isNotNull(params) && params instanceof IScopeEntity
            && StringUtils.isNotBlank(sqlString)) {
            IScopeEntity baseEntity = (IScopeEntity) params;
            baseEntity.setDataScope( " AND (" + sqlString.substring(4) + ")");
        }
    }

    /**
     * 拼接权限sql前先清空params.dataScope参数防止注入
     */
    private void clearDataScope(final JoinPoint joinPoint)
    {
        Object params = joinPoint.getArgs()[0];
        if (StringUtils.isNotNull(params) && params instanceof IScopeEntity)
        {
            IScopeEntity baseEntity = (IScopeEntity) params;
            baseEntity.setDataScope(null);
        }
    }
}
