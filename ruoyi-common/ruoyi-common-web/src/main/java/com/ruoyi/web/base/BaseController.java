package com.ruoyi.web.base;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.ruoyi.common.core.annotation.DataScope;
import com.ruoyi.common.core.domain.R;
import com.ruoyi.common.core.utils.ServletUtils;
import com.ruoyi.common.entity.CommonEntity;
import com.ruoyi.common.entity.PageData;
import com.ruoyi.common.log.annotation.Log;
import com.ruoyi.common.log.enums.BusinessType;
import com.ruoyi.common.security.annotation.BaseRequiresPermissions;
import com.ruoyi.common.security.utils.SecurityUtils;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.Map;

/**
 * web层通用数据处理
 * 
 * @author ruoyi
 */
public abstract class BaseController<T extends CommonEntity> implements BaseCModule<T>
{
    protected final Logger logger = LoggerFactory.getLogger(this.getClass());

    @BaseRequiresPermissions("list")
    @GetMapping("/list")
    @ApiOperation(value = "分页列表")
    @DataScope
    public R<PageData<T>> list(T queryData)
    {
        if(ignoreScope()) {
            queryData.setDataScope(null);
        }
        dealQueryParams(queryData);
        return listData(queryData);
    }

    protected boolean ignoreScope() {
        return true;
    }

    protected String getDateRangeField() {
        return "create_time";
    }

    protected R<PageData<T>> listData(T queryData) {
        return R.ok(getService().selectPage(queryData.getPageInfo(), getQueryWrapper(queryData)));
    }

    protected QueryWrapper<T> getQueryWrapper(T queryData) {
        QueryWrapper<T> baseWrapper = getService().getBaseWrapper(queryData);
        if(queryData != null && queryData.getQueryParams() != null && queryData.getQueryParams().size() > 0) {
            Object startDate = queryData.getQueryParam("startDate");
            Object endDate = queryData.getQueryParam("endDate");

            if(startDate != null) {
                baseWrapper.ge(getDateRangeField(), startDate.toString());
            }
            if(endDate != null) {
                baseWrapper.le(getDateRangeField(), endDate.toString());
            }
        }
        return baseWrapper;
    }

    /**
     * 获取详细信息
     */
//    @BaseRequiresPermissions("query")
    @GetMapping(value = "/{pk:\\d+}")
    @ApiOperation(value = "详情")
    public R<T> getInfo(@PathVariable Long pk)
    {
        return detail(pk);
    }
    protected R detail(Long pk) {
        return R.ok(getService().getById(pk));
    }

    /**
     * 新增
     */
    @BaseRequiresPermissions("add")
    @Log(businessType = BusinessType.INSERT)
    @PostMapping
    @ApiOperation(value = "添加")
    @Transactional(rollbackFor = Exception.class)
    public R add(@Validated @RequestBody T entity)
    {
        return save(entity);
    }

    protected R save(T entity) {
        return R.ok(getService().save(entity));
    }

    /**
     * 修改
     */
    @BaseRequiresPermissions("edit")
    @Log(businessType = BusinessType.UPDATE)
    @PutMapping
    @ApiOperation(value = "编辑")
    @Transactional(rollbackFor = Exception.class)
    public R edit(@Validated @RequestBody T entity)
    {
        return update(entity);
    }

    protected R update(T entity) {
        return R.ok(getService().updateById(entity));
    }

    /**
     * 删除
     */
    @BaseRequiresPermissions("remove")
    @Log(businessType = BusinessType.DELETE)
    @DeleteMapping("/{pks}")
    @ApiOperation(value = "删除")
    public R remove(@PathVariable Long[] pks)
    {
        return delete(pks);
    }

    protected R delete(Long[] pks) {
        getService().removeByIds(Arrays.asList(pks));
        return R.ok();
    }

    public void dealQueryParams(T t) {
        Map<String, String> queryParams = ServletUtils.parseMap(ServletUtils.getRequest());
        t.setQueryParams(queryParams);
    }

}
