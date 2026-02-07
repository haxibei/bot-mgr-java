package com.ruoyi.common.mp.service.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.TableInfo;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.extension.toolkit.SqlHelper;
import com.ruoyi.common.mp.dao.IBaseDao;
import com.ruoyi.common.core.utils.StringUtils;
import com.ruoyi.common.core.web.domain.PageInfo;
import com.ruoyi.common.entity.BaseEntity;
import com.ruoyi.common.entity.CommonEntity;
import com.ruoyi.common.entity.PageData;
import com.ruoyi.common.mp.service.IBaseService;
import com.ruoyi.common.mp.utils.MpUtil;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class BaseServiceImpl<T extends CommonEntity, M extends IBaseDao<T>> extends ServiceImpl<M, T> implements IBaseService<T> {


    /**
     * 如果有需求  重写该方法即可  默认支持  不为空的字段进行赋值查询
     *
     * @param t
     * @return
     */
    @Override
    public QueryWrapper<T> getBaseWrapper(T t) {
        QueryWrapper<T> queryWrapper = new QueryWrapper<T>();
        MpUtil.setBaseWrapper(queryWrapper, t);
        return queryWrapper;
    }

    @Override
    public T doSave(T t) {
        if (t.getPK() != null) {
            this.updateById(t);
        } else {
            this.save(t);
        }
        return t;
    }

    @Override
    public PageData<T> selectPage(T t) {
        QueryWrapper<T> wrapper = getBaseWrapper(t);

        if(StringUtils.isBlank(t.getOrdseg())) {//默认id倒叙
            TableInfo table = SqlHelper.table(t.getClass());
            String keyColumn = table.getKeyColumn();
            if(StringUtils.isNotEmpty(keyColumn)) {
                wrapper.orderByDesc(keyColumn);
            }
        }
        PageInfo pageInfo = t.getPageInfo() == null?new PageInfo():t.getPageInfo();
        return this.selectPage(pageInfo, wrapper);
    }

    @Override
    public PageData<T> selectPage(PageInfo pageInfo, Wrapper<T> baseWrapper) {
        pageInfo = pageInfo == null?new PageInfo(1, 12): pageInfo;
        Page<T> page = new Page<T>(pageInfo.getPage(), pageInfo.getLimit() < 0 ? 12 : pageInfo.getLimit());

        page = this.page(page, baseWrapper);

        return this.buildPageData(page);
    }

    public PageData<T> buildPageData(Page<T> dataPage) {
        return new PageData(dataPage.getRecords(), dataPage.getCurrent(), dataPage.getSize(), dataPage.getTotal(), dataPage.getPages());
    }

    @Override
    public List<T> selectList(T t) {
        QueryWrapper<T> wrapper = getBaseWrapper(t);
        if(StringUtils.isBlank(t.getOrdseg())) {//默认id倒叙
            TableInfo table = SqlHelper.table(t.getClass());
            String keyColumn = table.getKeyColumn();
            if(StringUtils.isNotEmpty(keyColumn)) {
                wrapper.orderByDesc(keyColumn);
            }
        }
        List<T> datas = this.list(wrapper);

        return datas == null ? new ArrayList<>() : datas;
    }

    @Override
    public List<T> selectTops(T t, Integer top) {
        t.setPage(1);
        t.setLimit(top);
        PageData<T> pageData = this.selectPage(t);

        List<T> datas = pageData.getRows();
        return datas == null ? new ArrayList<>() : datas;
    }

    /**
     * 该方法不支持联合主键
     *
     * @param entitys
     * @return
     */
    @Override
    public boolean doBatchSave(List<T> entitys) {

        return this.saveOrUpdateBatch(entitys);
    }

    @Override
    public Map<String, Object> statCount(T queryData, String... funcs) {

        if (funcs == null || funcs.length < 1) {
            log.warn("============== statCount must have one func at least, use default");
            funcs = new String[]{"count(1) totalCount"};
        }
        for (String func : funcs) {
            String lowerFuncs = func.toLowerCase();
            if (!lowerFuncs.contains("count") && !lowerFuncs.contains("sum")) {
                log.warn("============== statCount just support func [count] and [sum]");
                return null;
            }
        }

        QueryWrapper<T> queryWrapper = this.getBaseWrapper(queryData);
        queryWrapper.select(funcs);
        return this.getBaseMapper().statCount(queryWrapper);
    }

    @Override
    public boolean insertOrUpdate(T t) {
        return SqlHelper.retBool(this.getBaseMapper().insertOrUpdate(t));
    }

    @Override
    public boolean deleteDbById(Serializable pk) {
        return this.getBaseMapper().deleteDbById(pk);
    }
}
