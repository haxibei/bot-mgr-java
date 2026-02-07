package com.ruoyi.system.mapper;

import java.util.List;

import com.ruoyi.common.mp.dao.IBaseDao;
import com.ruoyi.system.domain.SysDictData;
import org.apache.ibatis.annotations.Param;

/**
 * 字典表 数据层
 * 
 * @author ruoyi
 */
public interface SysDictDataMapper extends IBaseDao<SysDictData>
{
    /**
     * 查询字典数据
     *
     * @param dictType 字典类型
     * @return 字典数据
     */
    int countDictDataByType(String dictType);


    /**
     * 同步修改字典类型
     *
     * @param oldDictType 旧字典类型
     * @param newDictType 新旧字典类型
     * @return 结果
     */
    int updateDictDataType(@Param("oldDictType") String oldDictType, @Param("newDictType") String newDictType);
}
