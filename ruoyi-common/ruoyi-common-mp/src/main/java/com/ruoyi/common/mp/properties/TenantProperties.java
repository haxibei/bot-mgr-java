package com.ruoyi.common.mp.properties;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;

import java.util.Arrays;
import java.util.List;

@Lazy(false)
@Configuration(
        proxyBeanMethods = false
)
@ConditionalOnProperty(
        name = {"mybatis-plus.tenant.enabled"},
        matchIfMissing = false
)
@ConfigurationProperties(prefix = "mybatis-plus.tenant")
public class TenantProperties {

    private String ignoreTables;

    public List<String> getIgnoreTables() {
        return Arrays.asList(ignoreTables.split(","));
    }

    public void setIgnoreTables(String ignoreTables) {
        this.ignoreTables = ignoreTables;
    }
}
