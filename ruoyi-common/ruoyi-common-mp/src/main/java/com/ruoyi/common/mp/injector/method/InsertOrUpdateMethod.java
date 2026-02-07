package com.ruoyi.common.mp.injector.method;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.core.injector.AbstractMethod;
import com.baomidou.mybatisplus.core.metadata.TableInfo;
import com.baomidou.mybatisplus.core.metadata.TableInfoHelper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.sql.SqlScriptUtils;
import org.apache.ibatis.executor.keygen.Jdbc3KeyGenerator;
import org.apache.ibatis.executor.keygen.KeyGenerator;
import org.apache.ibatis.executor.keygen.NoKeyGenerator;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlSource;

/**
 * 新增或者更新方法
 */
public class InsertOrUpdateMethod extends AbstractMethod {

    @Override
    public MappedStatement injectMappedStatement(Class<?> mapperClass, Class<?> modelClass, TableInfo tableInfo) {

        String method = "insertOrUpdate";
        String sqlTmpl = "<script>\nINSERT INTO %s %s VALUES %s\nON DUPLICATE KEY UPDATE %s\n</script> ";

        KeyGenerator keyGenerator = new NoKeyGenerator();
        String columnScript = SqlScriptUtils.convertTrim(tableInfo.getAllInsertSqlColumnMaybeIf(null), "(", ")", null, ",");
        String valuesScript = SqlScriptUtils.convertTrim(tableInfo.getAllInsertSqlPropertyMaybeIf(null), "(", ")", null, ",");
        String updateScript = SqlScriptUtils.convertTrim(this.sqlSet(true, false, tableInfo, false, null, null), "", "", null, ",");

        updateScript = updateScript.replaceAll("<set>", "").replaceAll("</set>", "");

        String keyProperty = null;
        String keyColumn = null;
        if (StringUtils.isNotBlank(tableInfo.getKeyProperty())) {
            if (tableInfo.getIdType() == IdType.AUTO) {
                keyGenerator = new Jdbc3KeyGenerator();
                keyProperty = tableInfo.getKeyProperty();
                keyColumn = tableInfo.getKeyColumn();
            } else if (null != tableInfo.getKeySequence()) {
                keyGenerator = TableInfoHelper.genKeyGenerator(method, tableInfo, this.builderAssistant);
                keyProperty = tableInfo.getKeyProperty();
                keyColumn = tableInfo.getKeyColumn();
            }
        }

        String sql = String.format(sqlTmpl, tableInfo.getTableName(), columnScript, valuesScript, updateScript);
        SqlSource sqlSource = this.languageDriver.createSqlSource(this.configuration, sql, modelClass);
        return this.addInsertMappedStatement(mapperClass, modelClass, method, sqlSource, keyGenerator, keyProperty, keyColumn);
    }
}
