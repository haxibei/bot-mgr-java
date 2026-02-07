package com.ruoyi.common.core.domain;

import com.alibaba.fastjson2.JSON;
import com.ruoyi.common.core.utils.StringUtils;
import lombok.Data;

@Data
public class DbModifyMsg {
		private String tableName;
		private String cmdType;
    	private String sql;
    	private String mapperName;
    	private String methodName;
    	private String operUid;
    	private Object param; //sql的条件参数
    	private Object data; //需要传送出去的相关数据内容
    	
    	public DbModifyMsg() {
		}
    	
    	public DbModifyMsg(String tableName, String cmdType, String sql, String mapperName, String methodName, String operUid, Object param, Object data) {
		    this.tableName = tableName;
			this.cmdType = cmdType;
		    this.sql = sql;
		    this.mapperName = mapperName;
		    this.methodName = methodName;
		    this.operUid = operUid;
		    this.param = param;
		    this.data = data;
    	}

		@Override
		public String toString() {
			return "tableName:" + tableName + ",cmdType:" + cmdType + ",sql:" + sql + ",mapperName:" + mapperName + ",methodName:" + methodName
					+ (StringUtils.isBlank(operUid) ? "" : ",operUid:" + operUid )
					+ (param == null ? "" : ",param:" + JSON.toJSONString(param) )
					+ (data == null ? "" : ",data:" + JSON.toJSONString(data) )
					;
		}
    }