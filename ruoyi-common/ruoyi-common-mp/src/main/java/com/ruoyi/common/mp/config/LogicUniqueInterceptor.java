package com.ruoyi.common.mp.config;

import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.core.toolkit.PluginUtils;
import com.baomidou.mybatisplus.extension.plugins.inner.InnerInterceptor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.*;

import java.sql.Connection;

@Slf4j
public class LogicUniqueInterceptor implements InnerInterceptor {

	public LogicUniqueInterceptor() {

	}
	
	@Override
	public void beforePrepare(StatementHandler sh, Connection connection, Integer transactionTimeout) {
		PluginUtils.MPStatementHandler mpSh = PluginUtils.mpStatementHandler(sh);
		MappedStatement ms = mpSh.mappedStatement();
		SqlCommandType sct = ms.getSqlCommandType();
		if (sct == SqlCommandType.UPDATE) {
			PluginUtils.MPBoundSql mpBs = mpSh.mPBoundSql();

			if(ms.getId().contains("delete")) {//逻辑删除的
				String sql = mpBs.sql();

				String reg = null;
				if(sql.contains("=unique")) {//逻辑删除的
					reg = "=unique";
				}else if(sql.contains("='unique'")) {//逻辑删除的
					reg = "='unique'";
				}
				if(StringUtils.isNotBlank(reg)) {
					String val = "="+IdWorker.getIdStr();
					String preSql = sql.replace(reg, val);

					mpBs.sql(preSql);
				}
			}

		}



	}
}
