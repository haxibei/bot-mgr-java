package com.ruoyi.common.mp.handler;

import com.baomidou.mybatisplus.extension.plugins.handler.TenantLineHandler;
import com.ruoyi.common.core.constant.SecurityConstants;
import com.ruoyi.common.core.context.SecurityContextHolder;
import com.ruoyi.common.mp.properties.TenantProperties;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.LongValue;

import javax.annotation.Resource;

public class CustomTenantHandler implements TenantLineHandler {

    private TenantProperties tenantProperties;

    @Override
    public Expression getTenantId() {
        // 假设有一个租户上下文，能够从中获取当前用户的租户
         Long tenantId = SecurityContextHolder.getTenantId();
        // 返回租户ID的表达式，LongValue 是 JSQLParser 中表示 bigint 类型的 class
        return new LongValue(tenantId);
    }

    @Override
    public String getTenantIdColumn() {
        return SecurityConstants.TENANT_ID;
    }

    @Override
    public boolean ignoreTable(String tableName) {

        //如果是超级管理员
        Long userId = SecurityContextHolder.getUserId();
        if((userId != null && 1L == userId) || SecurityContextHolder.isIgnoreTenantId()) {
            return true;
        }

        // 根据需要返回是否忽略该表
        return tenantProperties.getIgnoreTables() != null
                && tenantProperties.getIgnoreTables().indexOf(tableName) > -1;
    }

    public void setTenantProperties(TenantProperties tenantProperties) {
        this.tenantProperties = tenantProperties;
    }

}