package com.ruoyi.system.mapper;

import com.ruoyi.common.mp.dao.IBaseDao;
import com.ruoyi.system.domain.SysDictType;

import java.util.List;

/**
 * 字典表 数据层
 * 
 * @author ruoyi
 */
public interface SysDictTypeMapper extends IBaseDao<SysDictType>
{
    /**
     * 校验字典类型称是否唯一
     *
     * @param dictType 字典类型
     * @return 结果
     */
    SysDictType checkDictTypeUnique(String dictType);
}
