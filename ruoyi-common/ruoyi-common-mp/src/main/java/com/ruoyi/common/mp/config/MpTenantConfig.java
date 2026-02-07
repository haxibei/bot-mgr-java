package com.ruoyi.common.mp.config;

import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.TenantLineInnerInterceptor;
import com.ruoyi.common.mp.handler.CustomTenantHandler;
import com.ruoyi.common.mp.properties.TenantProperties;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;

/**
 * SpringDoc配置类
 *
 * @author ruoyi
 */
@Configuration(proxyBeanMethods = false)
@ConditionalOnProperty(value = "mybatis-plus.tenant.enabled")
public class MpTenantConfig implements InitializingBean
{
    @Resource
    private MybatisPlusInterceptor mybatisPlusInterceptor;

    @Resource
    private TenantProperties tenantProperties;

    /**
     * 在初始化后调用的方法
     */
    @Override
    public void afterPropertiesSet() {


        TenantLineInnerInterceptor tenantInterceptor = new TenantLineInnerInterceptor();
        CustomTenantHandler customTenantHandler = new CustomTenantHandler();
        customTenantHandler.setTenantProperties(tenantProperties);
        tenantInterceptor.setTenantLineHandler(customTenantHandler);
        mybatisPlusInterceptor.addInnerInterceptor(tenantInterceptor);
    }
}

