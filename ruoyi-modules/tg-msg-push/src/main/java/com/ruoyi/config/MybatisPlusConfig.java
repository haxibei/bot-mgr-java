package com.ruoyi.config;

import com.ruoyi.common.mp.config.AbstractMybatisPlusConfig;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * @author ruoyi
 * @since 1.0
 */
@Configuration
@EnableTransactionManagement
public class MybatisPlusConfig
		extends AbstractMybatisPlusConfig {
}
