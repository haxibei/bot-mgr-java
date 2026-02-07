package com.ruoyi.common.mp.dao;

import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface CustomerSqlMapper {

    List<Map<String, Object>> executeQuerySql(@Param("sql") String sql);

}
