package com.ruoyi.common.mp.config;

import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.baomidou.mybatisplus.extension.plugins.inner.InnerInterceptor;
import com.ruoyi.common.constant.DictSpace;
import com.ruoyi.common.core.context.SecurityContextHolder;
import com.ruoyi.common.core.domain.DbModifyMsg;
import com.ruoyi.common.core.domain.GlobalResultCode;
import com.ruoyi.common.core.exception.base.BaseException;
import com.ruoyi.common.core.utils.SpringUtils;
import com.ruoyi.common.redis.service.MessagePublisher;
import com.ruoyi.common.redis.service.RedisService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.ibatis.cache.CacheKey;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.*;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.RowBounds;
import org.apache.ibatis.type.TypeHandlerRegistry;

import java.sql.SQLException;
import java.text.DateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Slf4j
public class OperLogInterceptor implements InnerInterceptor {

	public final static String DB_KEY = "_dbkey"; //用于从MDC传递受影响行的主键id或能唯一定位到数据行的关键信息
	
	public OperLogInterceptor() {

	}
	
	@Override
	public void beforeUpdate(Executor executor, MappedStatement ms, Object parameter) throws SQLException {
		if (!SqlCommandType.SELECT.equals(ms.getSqlCommandType())) {
			String namespace = ms.getId();

			BoundSql boundSql = ms.getBoundSql(parameter);
	        // 执行的SQL语句
	        String originalSql = boundSql.getSql();
	        // SQL语句的参数
	        Object parameterObject = boundSql.getParameterObject();
	        if(parameterObject != null){
	            originalSql = showSql(ms.getConfiguration(), boundSql);
	        }
	        
	        String mapperName = namespace.substring(0,namespace.lastIndexOf("."));
        	String methodName= namespace.substring(namespace.lastIndexOf(".") + 1); //获取方法名
        	String operUid =SecurityContextHolder.getUserId() + ""; //当前操作人ID
        	
			DbModifyMsg modifyMsg = null;
	        if(SqlCommandType.DELETE.equals(ms.getSqlCommandType())) {
	        	String upperSql = originalSql.toUpperCase();
	        	if(!upperSql.contains("WHERE")) {
	        		throw new BaseException(GlobalResultCode.DB_OPERATE_ERROR.getCode()+"", GlobalResultCode.DB_OPERATE_ERROR.getMsg() + ",delete语句必须带条件");
	        	}
	        	String selSqlStr = upperSql.replaceFirst("DELETE", "SELECT * ");
	        	
	        	String mapKey = namespace + "_backup_4_delete";
	        	MappedStatement.Builder builder = new MappedStatement.Builder(ms.getConfiguration(), mapKey, ms.getSqlSource(), SqlCommandType.SELECT);
	        	builder.resource(ms.getResource());
	            builder.fetchSize(ms.getFetchSize());
	            builder.statementType(StatementType.STATEMENT);
	            builder.timeout(ms.getTimeout());
	            builder.parameterMap(ms.getParameterMap());
	            builder.resultMaps(Collections.singletonList(new ResultMap.Builder(ms.getConfiguration(), "backup_4_delete_" + Constants.MYBATIS_PLUS, HashMap.class, Collections.emptyList()).build()));
	            builder.resultSetType(ResultSetType.FORWARD_ONLY);
	            builder.cache(ms.getCache());
	            builder.flushCacheRequired(ms.isFlushCacheRequired());
	            builder.useCache(ms.isUseCache());
	            
	            MappedStatement selMS = builder.build();
	            
	            RowBounds rowBounds = new RowBounds(RowBounds.NO_ROW_OFFSET, RowBounds.NO_ROW_LIMIT);
	            BoundSql selSql = new BoundSql(selMS.getConfiguration(), selSqlStr.toLowerCase(), new ArrayList<ParameterMapping>(), parameter);
	            CacheKey cacheKey = executor.createCacheKey(selMS, parameter, rowBounds, selSql);

	            List<HashMap> resultMap = executor.query(selMS, null, rowBounds, null, cacheKey, selSql);
	        	
	            if(!CollectionUtils.isEmpty(resultMap)) {
	            	val2Str(resultMap);

	            	modifyMsg = new DbModifyMsg(getTableName(ms.getSqlCommandType(), originalSql), ms.getSqlCommandType().name(), originalSql, mapperName, methodName, operUid, parameter, resultMap);
	            	
	            	sendModifyMsg(modifyMsg);
	            }
	        } else {
	        	modifyMsg = new DbModifyMsg(getTableName(ms.getSqlCommandType(), originalSql), ms.getSqlCommandType().name(), originalSql, mapperName, methodName, operUid, parameter, parameterObject);
	        	sendModifyMsg(modifyMsg);

	        }
		}
	}

	private static List<String> tables = Arrays.stream(DictSpace.values()).map(DictSpace::getTable).collect(Collectors.toList());
	private void sendModifyMsg(DbModifyMsg modifyMsg) {
		//TODO 发送消息
		log.info("==============="+JSON.toJSONString(modifyMsg));
		if(CollectionUtils.isNotEmpty(tables) && tables.contains(modifyMsg.getTableName())) {
			try {
				RedisService redisService = SpringUtils.getBean("redisService");
				MessagePublisher publisher = MessagePublisher.get(SecurityContextHolder.getApplicationName());

				if(publisher != null) {
					redisService.publishDbMsg(publisher, modifyMsg);
				}
			} catch (Exception e) {
				log.error("数据变更,发送消息失败", e);
			}
		}

	}
	
	@SuppressWarnings("all")
	private void val2Str(List<HashMap> resultMap) {
		for(HashMap lineMap : resultMap) {
			for(Object key : lineMap.keySet()) {
				Object value = lineMap.get(key);
				
				if(value != null) {
					lineMap.put(key, value.toString());
				}
			}
		}
	}

	private String getTableName(SqlCommandType type, String sql) {
		sql = sql.toUpperCase();
		String word = "FROM"; //select, delete

		if(SqlCommandType.INSERT.equals(type)) {
			word = "INSERT INTO";
		}else if(SqlCommandType.UPDATE.equals(type)) {
			word = "UPDATE";
		}

		// 解析SQL语句，获取操作的表名称
		Pattern pattern = Pattern.compile(word + "\\s+[`,\\w]+", Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(sql);
		if (matcher.find()) {
			String tableName = matcher.group().replaceAll(word + "\\s+", "");
			return tableName.toLowerCase();
		}
		return null;
	}
	
	/**
     * 获取参数值
     * @param obj
     * @return
     */
    private static String getParameterValue(Object obj) {
        String value = null;
        if (obj instanceof String) {
            value = "'" + obj + "'";
        } else if (obj instanceof Date) {
            DateFormat formatter = DateFormat.getDateTimeInstance(DateFormat.DEFAULT, DateFormat.DEFAULT, Locale.CHINA);
            value = "to_date('" + formatter.format(obj) + "','yyyy-MM-dd hh24:mi:ss')";
        } else {
            if (obj != null) {
                value = obj.toString();
            } else {
                value = "";
            }
 
        }
//        System.err.println("获取值: "+value);
        return quoteReplacement(value);
    }
 
    /***
     * sql 参数替换
     * @param configuration
     * @param boundSql
     * @return
     */
    private static String showSql(Configuration configuration, BoundSql boundSql) {
        //获取参数对象
        Object parameterObject = boundSql.getParameterObject();
        //获取当前的sql语句有绑定的所有parameterMapping属性
        List<ParameterMapping> parameterMappings = boundSql.getParameterMappings();
        //去除空格
        String sql = boundSql.getSql().replaceAll("[\\s]+", " ");
        if (parameterMappings.size() > 0 && parameterObject != null) {
            TypeHandlerRegistry typeHandlerRegistry = configuration.getTypeHandlerRegistry();
            /* 如果参数满足：org.apache.ibatis.type.TypeHandlerRegistry#hasTypeHandler(java.lang.Class<?>)
            org.apache.ibatis.type.TypeHandlerRegistry#TYPE_HANDLER_MAP
            * 即是不是属于注册类型(TYPE_HANDLER_MAP...等/有没有相应的类型处理器)
             * */
            if (typeHandlerRegistry.hasTypeHandler(parameterObject.getClass())) {
                sql = sql.replaceFirst("\\?", getParameterValue(parameterObject));
            } else {
                //装饰器，可直接操作属性值 ---》 以parameterObject创建装饰器
                //MetaObject 是 Mybatis 反射工具类，通过 MetaObject 获取和设置对象的属性值
                MetaObject metaObject = configuration.newMetaObject(parameterObject);
                
                //循环 parameterMappings 所有属性
                for (ParameterMapping parameterMapping : parameterMappings) {
                    //获取property属性
                    String propertyName = parameterMapping.getProperty();
//                    System.err.println("propertyName: "+propertyName);
                    //是否声明了propertyName的属性和get方法
                    if (metaObject.hasGetter(propertyName)) {
                        Object obj = metaObject.getValue(propertyName);
                        sql = sql.replaceFirst("\\?", getParameterValue(obj));
                    } else if (boundSql.hasAdditionalParameter(propertyName)) {
                        //判断是不是sql的附加参数
                        Object obj = boundSql.getAdditionalParameter(propertyName);
                        sql = sql.replaceFirst("\\?", getParameterValue(obj));
                    }
                }
            }
        }
        return sql;
    }
    
    private static String quoteReplacement(String s) {
        if ((s.indexOf('\\') == -1) && (s.indexOf('$') == -1))  
            return s;  
        StringBuffer sb = new StringBuffer();
        for (int i=0; i<s.length(); i++) {  
            char c = s.charAt(i);  
            if (c == '\\') {  
                sb.append('\\'); sb.append('\\');  
            } else if (c == '$') {  
                sb.append('\\'); sb.append('$');  
            } else {  
                sb.append(c);  
            }  
        }  
        return sb.toString();  
    }  

    
}
