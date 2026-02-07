package com.ruoyi.common.mp.handler;

import com.ruoyi.common.core.utils.DataCryptUtils;
import com.ruoyi.common.core.utils.SpringUtils;
import com.ruoyi.common.entity.CryptString;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedJdbcTypes;
import org.apache.ibatis.type.MappedTypes;
import org.springframework.beans.factory.annotation.Autowired;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * <h2>字段加密处理器</h2>
 */
@MappedTypes(value = CryptString.class)
@MappedJdbcTypes(value = JdbcType.VARCHAR, includeNullJdbcType = true)
public class FieldCryptHandler extends BaseTypeHandler<CryptString> {

	@Autowired
	private DataCryptUtils dataCryptUtils;

	@Override
	public void setParameter(PreparedStatement preparedStatement, int index, CryptString parameter, JdbcType jdbcType) throws SQLException {
		preparedStatement.setString(index, encryptData(parameter));
	}

	@Override
	public void setNonNullParameter(PreparedStatement preparedStatement, int index, CryptString parameter, JdbcType jdbcType)
			throws SQLException {
		preparedStatement.setString(index, encryptData(parameter));
	}

	@Override
	public CryptString getNullableResult(ResultSet resultSet, String columnName)
			throws SQLException {
		String value = resultSet.getString(columnName);
		return decryptData(value);
	}

	@Override
	public CryptString getNullableResult(ResultSet resultSet, int columnIndex)
			throws SQLException {
		String value = resultSet.getString(columnIndex);
		return decryptData(value);
	}

	@Override
	public CryptString getNullableResult(CallableStatement callableStatement, int columnIndex)
			throws SQLException {
		String value = callableStatement.getString(columnIndex);
		return decryptData(value);
	}

	private String encryptData(CryptString phone) {
		if(dataCryptUtils == null) {
			dataCryptUtils = SpringUtils.getBean("dataCryptUtils");
		}
		return dataCryptUtils.encryptData(phone.toString());
	}

	private CryptString decryptData(String phone) {
		if(phone == null) {
			return null;
		}
		if(dataCryptUtils == null) {
			dataCryptUtils = SpringUtils.getBean("dataCryptUtils");
		}
		return new CryptString(dataCryptUtils.decryptData(phone));
	}
}
