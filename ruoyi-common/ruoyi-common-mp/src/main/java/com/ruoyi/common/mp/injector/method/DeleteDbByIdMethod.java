package com.ruoyi.common.mp.injector.method;

import com.baomidou.mybatisplus.core.injector.AbstractMethod;
import com.baomidou.mybatisplus.core.metadata.TableInfo;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlSource;

/**
 * 物理方法
 */
public class DeleteDbByIdMethod extends AbstractMethod {

    @Override
    public MappedStatement injectMappedStatement(Class<?> mapperClass, Class<?> modelClass, TableInfo tableInfo) {
        String method = "deleteDbById";

        String sqlTmp = "<script>\nDELETE FROM %s WHERE %s=#{%s}\n</script>";
        String sql = String.format(sqlTmp, tableInfo.getTableName(), tableInfo.getKeyColumn(),
                tableInfo.getKeyProperty());
        SqlSource sqlSource = languageDriver.createSqlSource(configuration, sql.toLowerCase(), Object.class);
        return this.addDeleteMappedStatement(mapperClass, method, sqlSource);
    }
}
