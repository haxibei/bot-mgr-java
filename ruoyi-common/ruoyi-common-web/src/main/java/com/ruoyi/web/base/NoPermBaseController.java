package com.ruoyi.web.base;

import com.ruoyi.common.core.annotation.DataScope;
import com.ruoyi.common.core.domain.R;
import com.ruoyi.common.core.utils.ServletUtils;
import com.ruoyi.common.entity.CommonEntity;
import com.ruoyi.common.entity.PageData;
import com.ruoyi.common.log.annotation.Log;
import com.ruoyi.common.log.enums.BusinessType;
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
public abstract class NoPermBaseController<T extends CommonEntity> implements BaseCModule<T>
{
    protected final Logger logger = LoggerFactory.getLogger(this.getClass());

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

    protected R<PageData<T>> listData(T queryData) {
        return R.ok(getService().selectPage(queryData));
    }

    /**
     * 获取详细信息
     */
    @GetMapping(value = "/{pk}")
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
