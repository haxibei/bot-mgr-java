package com.ruoyi.common.mp.service;


import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ruoyi.common.core.web.domain.PageInfo;
import com.ruoyi.common.entity.BaseEntity;
import com.ruoyi.common.entity.CommonEntity;
import com.ruoyi.common.entity.PageData;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public interface IBaseService<T extends CommonEntity> extends IService<T> {

    QueryWrapper<T> getBaseWrapper(T t);

    T doSave(T t);

    boolean doBatchSave(List<T> entitys);

    PageData<T> selectPage(T t);

    PageData<T> selectPage(PageInfo pageInfo, Wrapper<T> baseWrapper);

    List<T> selectList(T t);

    /**
     * @param t   查询条件
     * @param top 前多少条
     * @return
     */
    List<T> selectTops(T t, Integer top);

    /**
     * 查询 聚合函数  得到一个map
     *
     * @param queryData 实体类
     * @param funcs     聚合函数   形如  sum(xx) xx, count(1) yy, 没有 默认一个 count(1) totalCount
     * @return
     */
    Map<String, Object> statCount(T queryData, String... funcs);

    boolean insertOrUpdate(T t);

    boolean deleteDbById(Serializable pk);
}
