package com.ruoyi.system.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.ruoyi.common.core.constant.UserConstants;
import com.ruoyi.common.core.domain.R;
import com.ruoyi.common.core.utils.StringUtils;
import com.ruoyi.common.domain.SysDept;
import com.ruoyi.common.log.annotation.Log;
import com.ruoyi.common.log.enums.BusinessType;
import com.ruoyi.common.security.annotation.RequiresPermissions;
import com.ruoyi.system.service.ISysDeptService;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 部门信息
 * 
 * @author ruoyi
 */
@RestController
@RequestMapping("/dept")
public class SysDeptController
{
    @Autowired
    private ISysDeptService deptService;

    /**
     * 获取部门列表
     */
    @RequiresPermissions("system:dept:list")
    @GetMapping("/list")
    public R<List<SysDept>> list(SysDept dept)
    {
        List<SysDept> depts = deptService.selectDeptList(dept);
        return R.ok(depts);
    }

    @GetMapping("listAll")
    @ApiOperation("一级部门 下拉选项")
    public R<List<SysDept>> listAll()
    {
        SysDept queryData = new SysDept();
        queryData.setOrdseg("order_num");
        QueryWrapper<SysDept> baseWrapper = deptService.getBaseWrapper(queryData);
        baseWrapper.lambda().select(SysDept::getDeptId,SysDept::getDeptName).eq(SysDept::getParentId, 1);
        List<SysDept> list = deptService.list(baseWrapper);
        return R.ok(list);
    }

    /**
     * 查询部门列表（排除节点）
     */
    @RequiresPermissions("system:dept:list")
    @GetMapping("/list/exclude/{deptId}")
    public R<List<SysDept>> excludeChild(@PathVariable(value = "deptId", required = false) Long deptId)
    {

        List<SysDept> depts = deptService.selectDeptList(new SysDept());
        depts.removeIf(d -> d.getDeptId().intValue() == deptId || ArrayUtils.contains(StringUtils.split(d.getAncestors(), ","), deptId + ""));
        return R.ok(depts);
    }

    /**
     * 根据部门编号获取详细信息
     */
    @RequiresPermissions("system:dept:query")
    @GetMapping(value = "/{deptId}")
    public R<SysDept> getInfo(@PathVariable Long deptId)
    {
        deptService.checkDeptDataScope(deptId);
        return R.ok(deptService.selectDeptById(deptId));
    }

    /**
     * 新增部门
     */
    @RequiresPermissions("system:dept:add")
    @Log(title = "部门管理", businessType = BusinessType.INSERT)
    @PostMapping
    public R<Integer> add(@Validated @RequestBody SysDept dept)
    {
        if (!deptService.checkDeptNameUnique(dept))
        {
            return R.fail("新增部门'" + dept.getDeptName() + "'失败，部门名称已存在");
        }
        return R.ok(deptService.insertDept(dept));
    }

    /**
     * 修改部门
     */
    @RequiresPermissions("system:dept:edit")
    @Log(title = "部门管理", businessType = BusinessType.UPDATE)
    @PutMapping
    public R<Integer> edit(@Validated @RequestBody SysDept dept)
    {
        Long deptId = dept.getDeptId();
        deptService.checkDeptDataScope(deptId);
        if (!deptService.checkDeptNameUnique(dept))
        {
            return R.fail("修改部门'" + dept.getDeptName() + "'失败，部门名称已存在");
        }
        else if (dept.getParentId().equals(deptId))
        {
            return R.fail("修改部门'" + dept.getDeptName() + "'失败，上级部门不能是自己");
        }
        else if (StringUtils.equals(UserConstants.DEPT_DISABLE, dept.getStatus()) && deptService.selectNormalChildrenDeptById(deptId) > 0)
        {
            return R.fail("该部门包含未停用的子部门！");
        }
        return R.ok(deptService.updateDept(dept));
    }

    /**
     * 删除部门
     */
    @RequiresPermissions("system:dept:remove")
    @Log(title = "部门管理", businessType = BusinessType.DELETE)
    @DeleteMapping("/{deptId}")
    public R<Integer> remove(@PathVariable Long deptId)
    {
        if (deptService.hasChildByDeptId(deptId))
        {
            return R.fail("存在下级部门,不允许删除");
        }
        if (deptService.checkDeptExistUser(deptId))
        {
            return R.fail("部门存在用户,不允许删除");
        }
        deptService.checkDeptDataScope(deptId);
        return R.ok(deptService.deleteDeptById(deptId));
    }
}
