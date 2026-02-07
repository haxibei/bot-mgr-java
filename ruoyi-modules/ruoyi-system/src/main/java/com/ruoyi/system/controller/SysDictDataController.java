package com.ruoyi.system.controller;

import com.ruoyi.common.constant.*;
import com.ruoyi.common.core.domain.R;
import com.ruoyi.common.core.utils.StringUtils;
import com.ruoyi.common.core.utils.poi.ExcelUtil;
import com.ruoyi.common.entity.PageData;
import com.ruoyi.common.log.annotation.Log;
import com.ruoyi.common.log.enums.BusinessType;
import com.ruoyi.common.security.annotation.RequiresPermissions;
import com.ruoyi.system.domain.SysDictData;
import com.ruoyi.system.service.ISysDictDataService;
import com.ruoyi.system.service.ISysDictTypeService;
import io.swagger.annotations.ApiModelProperty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.*;

/**
 * 数据字典信息
 * 
 * @author ruoyi
 */
@RestController
@RequestMapping("/dict/data")
public class SysDictDataController
{
    @Autowired
    private ISysDictDataService dictDataService;

    @Autowired
    private ISysDictTypeService dictTypeService;

    @RequiresPermissions("system:dict:list")
    @GetMapping("/list")
    public R<PageData<SysDictData>> list(SysDictData dictData)
    {
        PageData<SysDictData> list = dictDataService.selectDictDataPage(dictData);
        return R.ok(list);
    }

    @Log(title = "字典数据", businessType = BusinessType.EXPORT)
    @RequiresPermissions("system:dict:export")
    @PostMapping("/export")
    public void export(HttpServletResponse response, SysDictData dictData)
    {
        List<SysDictData> list = dictDataService.selectDictDataList(dictData);
        ExcelUtil<SysDictData> util = new ExcelUtil<SysDictData>(SysDictData.class);
        util.exportExcel(response, list, "字典数据");
    }

    /**
     * 查询字典数据详细
     */
    @RequiresPermissions("system:dict:query")
    @GetMapping(value = "/{dictCode}")
    public R<SysDictData> getInfo(@PathVariable Long dictCode)
    {
        return R.ok(dictDataService.selectDictDataById(dictCode));
    }

    /**
     * 根据字典类型查询字典数据信息
     */
    @GetMapping(value = "/type/{dictType}")
    public R<List<SysDictData>> dictType(@PathVariable String dictType)
    {
        List<SysDictData> data = dictTypeService.selectDictDataByType(dictType);
        if (StringUtils.isNull(data))
        {
            data = new ArrayList<SysDictData>();
        }
        return R.ok(data);
    }

    /**
     * 新增字典类型
     */
    @RequiresPermissions("system:dict:add")
    @Log(title = "字典数据", businessType = BusinessType.INSERT)
    @PostMapping
    public R<Integer> add(@Validated @RequestBody SysDictData dict)
    {
        return R.ok(dictDataService.insertDictData(dict));
    }

    /**
     * 修改保存字典类型
     */
    @RequiresPermissions("system:dict:edit")
    @Log(title = "字典数据", businessType = BusinessType.UPDATE)
    @PutMapping
    public R<Integer> edit(@Validated @RequestBody SysDictData dict)
    {
        return R.ok(dictDataService.updateDictData(dict));
    }

    /**
     * 删除字典类型
     */
    @RequiresPermissions("system:dict:remove")
    @Log(title = "字典类型", businessType = BusinessType.DELETE)
    @DeleteMapping("/{dictCodes}")
    public R remove(@PathVariable Long[] dictCodes)
    {
        dictDataService.deleteDictDataByIds(dictCodes);
        return R.ok();
    }

    @GetMapping(value = "/enum/{enumName}")
    @ApiModelProperty("系统枚举字典,支持参数 bool,")
    public R<List<BaseEnum>> enumType(@PathVariable String enumName)
    {
        if(StringUtils.isBlank(enumName)) {
            return R.ok(new ArrayList<>());
        }
        List<BaseEnum> baseEnum = enums.get(enumName);
        return R.ok(baseEnum == null?new ArrayList<>():baseEnum);
    }

    private static Map<String, List<BaseEnum>> enums = new HashMap<>();
    static {
        enums.put("putOnState", Arrays.asList(PutOnState.values()));
        enums.put("bool", Arrays.asList(Bool.values()));
    }
}
