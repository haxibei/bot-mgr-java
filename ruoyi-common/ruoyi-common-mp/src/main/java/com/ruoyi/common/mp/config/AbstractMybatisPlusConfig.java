package com.ruoyi.common.mp.config;


import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.TenantLineInnerInterceptor;
import com.ruoyi.common.core.constant.SecurityConstants;
import com.ruoyi.common.core.context.SecurityContextHolder;
import com.ruoyi.common.core.utils.StringUtils;
import com.ruoyi.common.mp.handler.CustomTenantHandler;
import com.ruoyi.common.mp.properties.TenantProperties;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;

import java.time.LocalDateTime;

/**
 * @author ruoyi
 * @since 1.0
 */
@Slf4j
abstract public class AbstractMybatisPlusConfig {
	
	@Value("${mybatis.modify.binder.name:dbmodify-out-0}")
	private String dbModifyBinderName;

    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        interceptor.addInnerInterceptor(new PaginationInnerInterceptor(DbType.MYSQL));
        interceptor.addInnerInterceptor(new OperLogInterceptor());
		interceptor.addInnerInterceptor(new LogicUniqueInterceptor());
        return interceptor;
    }
    
    @Bean(value = "AutoFillHandler")
    public MetaObjectHandler createAutoFillHandlerBean() {
        return new AutoFillHandler();
    }

    public final static class AutoFillHandler implements MetaObjectHandler {

		@Override
		public void insertFill(MetaObject metaObject) {
			log.debug("MyBatisPlus Insert 自动填充之前: {}", metaObject.getOriginalObject());
			if (AutoFillHandler.has(metaObject, "createBy")) {
				if(metaObject.getValue("createBy") == null) {
					this.strictInsertFill(metaObject, "createBy", SecurityContextHolder:: getUserId, Long.class);
				}
			}
			if (AutoFillHandler.has(metaObject, "deptId")) {
				if(metaObject.getValue("deptId") == null) {
					this.strictInsertFill(metaObject, "deptId", SecurityContextHolder:: getDeptId, Long.class);
				}
			}

			if (AutoFillHandler.has(metaObject, "updateBy"))
				this.strictInsertFill(metaObject, "updateBy", SecurityContextHolder:: getUserId, Long.class);
			if (AutoFillHandler.has(metaObject, "createTime"))
				this.strictInsertFill(metaObject, "createTime", LocalDateTime:: now, LocalDateTime.class);
			if (AutoFillHandler.has(metaObject, "updateTime"))
				this.strictUpdateFill(metaObject, "updateTime", LocalDateTime:: now, LocalDateTime.class);
			if (AutoFillHandler.has(metaObject, SecurityConstants.TENANT_ID))
				this.strictUpdateFill(metaObject, StringUtils.convertToCamelCase(SecurityConstants.TENANT_ID), SecurityContextHolder::getTenantId, Long.class);

			log.debug("MyBatisPlus Insert 自动填充之后: {}", metaObject.getOriginalObject());
		}
		
		@Override
		public void updateFill(MetaObject metaObject) {
			log.debug("MyBatisPlus Update 自动填充之前: {}", metaObject.getOriginalObject());

			if (AutoFillHandler.has(metaObject, "operateBy"))
				this.strictInsertFill(metaObject, "operateBy", SecurityContextHolder:: getUserId, Long.class);
			if (AutoFillHandler.has(metaObject, "updateBy"))
				this.strictInsertFill(metaObject, "updateBy", SecurityContextHolder:: getUserId, Long.class);
			if (AutoFillHandler.has(metaObject, "updateTime"))
				this.strictUpdateFill(metaObject, "updateTime", LocalDateTime:: now, LocalDateTime.class);
			
			log.debug("MyBatisPlus Update 自动填充之后: {}", metaObject.getOriginalObject());
		}
		
		static Boolean has(MetaObject metaObject, String name) {
			return metaObject.hasSetter(name) && metaObject.hasGetter(name);
		}
    }
}
