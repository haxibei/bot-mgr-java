package com.ruoyi.system.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.ruoyi.common.constant.SystemRoleKey;
import com.ruoyi.common.core.domain.R;
import com.ruoyi.common.core.utils.StringUtils;
import com.ruoyi.common.core.utils.poi.ExcelUtil;
import com.ruoyi.common.domain.SysRole;
import com.ruoyi.common.entity.PageData;
import com.ruoyi.common.log.annotation.Log;
import com.ruoyi.common.log.enums.BusinessType;
import com.ruoyi.common.security.annotation.RequiresPermissions;
import com.ruoyi.common.security.utils.SecurityUtils;
import com.ruoyi.system.domain.SysDictType;
import com.ruoyi.system.service.ISysDictTypeService;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 数据字典信息
 * 
 * @author ruoyi
 */
@RestController
@RequestMapping("/dict/type")
public class SysDictTypeController
{
    @Autowired
    private ISysDictTypeService dictTypeService;

    @RequiresPermissions("system:dict:list")
    @GetMapping("/list")
    public R<PageData<SysDictType>> list(SysDictType dictType)
    {
        List<SysRole> roles = SecurityUtils.getLoginUser().getSysUser().getRoles();
        String category = null;
        if(CollectionUtils.isNotEmpty(roles)) {
            for(SysRole role : roles) {
                if(SystemRoleKey.AdOperator.getRoleKey().equals(role.getRoleKey())
                    || SystemRoleKey.AdFinance.getRoleKey().equals(role.getRoleKey())) {
                    category = role.getRoleKey();
                }
            }
        }
        if(StringUtils.isNoneBlank(category)) {
            dictType.setCategory(category);
            PageData<SysDictType> list = dictTypeService.selectDictTypePage(dictType);
            return R.ok(list);
        }else {
            PageData<SysDictType> list = dictTypeService.selectDictTypePage(dictType);
            return R.ok(list);
        }
    }

    @Log(title = "字典类型", businessType = BusinessType.EXPORT)
    @RequiresPermissions("system:dict:export")
    @PostMapping("/export")
    public void export(HttpServletResponse response, SysDictType dictType)
    {
        List<SysDictType> list = dictTypeService.selectList(dictType);
        ExcelUtil<SysDictType> util = new ExcelUtil<SysDictType>(SysDictType.class);
        util.exportExcel(response, list, "字典类型");
    }

    /**
     * 查询字典类型详细
     */
    @RequiresPermissions("system:dict:query")
    @GetMapping(value = "/{dictId}")
    public R<SysDictType> getInfo(@PathVariable Long dictId)
    {
        return R.ok(dictTypeService.selectDictTypeById(dictId));
    }

    /**
     * 新增字典类型
     */
    @RequiresPermissions("system:dict:add")
    @Log(title = "字典类型", businessType = BusinessType.INSERT)
    @PostMapping
    public R<Integer> add(@Validated @RequestBody SysDictType dict)
    {
        if (!dictTypeService.checkDictTypeUnique(dict))
        {
            return R.fail("新增字典'" + dict.getDictName() + "'失败，字典类型已存在");
        }
        return R.ok(dictTypeService.insertDictType(dict));
    }

    /**
     * 修改字典类型
     */
    @RequiresPermissions("system:dict:edit")
    @Log(title = "字典类型", businessType = BusinessType.UPDATE)
    @PutMapping
    public R<Integer> edit(@Validated @RequestBody SysDictType dict)
    {
        if (!dictTypeService.checkDictTypeUnique(dict))
        {
            return R.fail("修改字典'" + dict.getDictName() + "'失败，字典类型已存在");
        }
        return R.ok(dictTypeService.updateDictType(dict));
    }

    /**
     * 删除字典类型
     */
    @RequiresPermissions("system:dict:remove")
    @Log(title = "字典类型", businessType = BusinessType.DELETE)
    @DeleteMapping("/{dictIds}")
    public R remove(@PathVariable Long[] dictIds)
    {
        dictTypeService.deleteDictTypeByIds(dictIds);
        return R.ok();
    }

    /**
     * 刷新字典缓存
     */
    @RequiresPermissions("system:dict:remove")
    @Log(title = "字典类型", businessType = BusinessType.CLEAN)
    @DeleteMapping("/refreshCache")
    public R refreshCache()
    {
        dictTypeService.resetDictCache();
        return R.ok();
    }

    /**
     * 获取字典选择框列表
     */
    @GetMapping("/optionselect")
    public R<List<SysDictType>> optionselect()
    {
        List<SysDictType> dictTypes = dictTypeService.selectDictTypeAll();
        return R.ok(dictTypes);
    }
}
