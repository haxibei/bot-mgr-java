package com.ruoyi.common.mp.dao;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.ruoyi.common.entity.CommonEntity;
import org.apache.ibatis.annotations.Param;

import java.io.Serializable;
import java.util.Map;

public interface IBaseDao<T extends CommonEntity> extends BaseMapper<T> {

    int insertOrUpdate(T t);

    /**
     * 得到一个map
     * @param queryWrapper
     * @return
     */
    Map<String, Object> statCount(@Param(Constants.WRAPPER) Wrapper<T> queryWrapper);

    boolean deleteDbById(Serializable pk);
}
