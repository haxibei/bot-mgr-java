package com.ruoyi.system.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.ruoyi.common.core.annotation.DataScope;
import com.ruoyi.common.core.domain.R;
import com.ruoyi.common.core.enums.UserStatus;
import com.ruoyi.common.core.exception.base.BaseException;
import com.ruoyi.common.core.utils.StringUtils;
import com.ruoyi.common.core.utils.poi.ExcelUtil;
import com.ruoyi.common.domain.SysDept;
import com.ruoyi.common.domain.SysRole;
import com.ruoyi.common.domain.SysUser;
import com.ruoyi.common.entity.PageData;
import com.ruoyi.common.log.annotation.Log;
import com.ruoyi.common.log.enums.BusinessType;
import com.ruoyi.common.security.annotation.RequiresPermissions;
import com.ruoyi.common.security.utils.SecurityUtils;
import com.ruoyi.system.domain.SysUserRole;
import com.ruoyi.system.service.ISysDeptService;
import com.ruoyi.system.service.ISysRoleService;
import com.ruoyi.system.service.ISysUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 角色信息
 * 
 * @author ruoyi
 */
@RestController
@RequestMapping("/role")
public class SysRoleController
{
    @Autowired
    private ISysRoleService roleService;

    @Autowired
    private ISysUserService userService;

    @Autowired
    private ISysDeptService deptService;

    @RequiresPermissions("system:role:list")
    @GetMapping("/list")
    public R<PageData<SysRole>> list(SysRole role)
    {
        role.setOrdseg("role_sort");
        PageData<SysRole> list = roleService.selectRolePage(role);
        return R.ok(list);
    }

    @RequiresPermissions("system:role:list")
    @GetMapping("listAll")
    public R<List<SysRole>> listAll(String filterAudit)
    {
        SysRole queryData = new SysRole();
        queryData.setOrdseg("role_id");
        QueryWrapper<SysRole> baseWrapper = roleService.getBaseWrapper(queryData);
        if(StringUtils.isNotBlank(filterAudit)) {//只需要审核角色
            baseWrapper.eq("audit_role", 1);
        }
        baseWrapper.lambda().select(SysRole::getRoleId,SysRole::getRoleKey,SysRole::getRoleName);
        List<SysRole> list = roleService.list(baseWrapper);
        return R.ok(list);
    }

    @Log(title = "角色管理", businessType = BusinessType.EXPORT)
    @RequiresPermissions("system:role:export")
    @PostMapping("/export")
    public void export(HttpServletResponse response, SysRole role)
    {
        role.setOrdseg("role_id");
        List<SysRole> list = roleService.selectRoleList(role);
        ExcelUtil<SysRole> util = new ExcelUtil<SysRole>(SysRole.class);
        util.exportExcel(response, list, "角色数据");
    }

    /**
     * 根据角色编号获取详细信息
     */
    @RequiresPermissions("system:role:query")
    @GetMapping(value = "/{roleId}")
    public R getInfo(@PathVariable Long roleId)
    {
        roleService.checkRoleDataScope(roleId);
        return R.ok(roleService.selectRoleById(roleId));
    }

    /**
     * 新增角色
     */
    @RequiresPermissions("system:role:add")
    @Log(title = "角色管理", businessType = BusinessType.INSERT)
    @PostMapping
    public R add(@Validated @RequestBody SysRole role)
    {
        if (!roleService.checkRoleNameUnique(role))
        {
            return R.fail("新增角色'" + role.getRoleName() + "'失败，角色名称已存在");
        }
        else if (!roleService.checkRoleKeyUnique(role))
        {
            return R.fail("新增角色'" + role.getRoleName() + "'失败，角色权限已存在");
        }
        return R.ok(roleService.insertRole(role));

    }

    /**
     * 修改保存角色
     */
    @RequiresPermissions("system:role:edit")
    @Log(title = "角色管理", businessType = BusinessType.UPDATE)
    @PutMapping
    public R edit(@Validated @RequestBody SysRole role)
    {
        roleService.checkRoleAllowed(role);
        roleService.checkRoleDataScope(role.getRoleId());
        if (!roleService.checkRoleNameUnique(role))
        {
            return R.fail("修改角色'" + role.getRoleName() + "'失败，角色名称已存在");
        }
        else if (!roleService.checkRoleKeyUnique(role))
        {
            return R.fail("修改角色'" + role.getRoleName() + "'失败，角色权限已存在");
        }
        return R.ok(roleService.updateRole(role));
    }

    /**
     * 修改保存数据权限
     */
    @RequiresPermissions("system:role:edit")
    @Log(title = "角色管理", businessType = BusinessType.UPDATE)
    @PutMapping("/dataScope")
    public R dataScope(@RequestBody SysRole role)
    {
        roleService.checkRoleAllowed(role);
        roleService.checkRoleDataScope(role.getRoleId());
        return R.ok(roleService.authDataScope(role));
    }

    /**
     * 状态修改
     */
    @RequiresPermissions("system:role:edit")
    @Log(title = "角色管理", businessType = BusinessType.UPDATE)
    @PutMapping("/changeStatus")
    public R changeStatus(@RequestBody SysRole role)
    {
        roleService.checkRoleAllowed(role);
        roleService.checkRoleDataScope(role.getRoleId());
        return R.ok(roleService.updateRoleStatus(role));
    }

    /**
     * 删除角色
     */
    @RequiresPermissions("system:role:remove")
    @Log(title = "角色管理", businessType = BusinessType.DELETE)
    @DeleteMapping("/{roleIds}")
    public R remove(@PathVariable Long[] roleIds)
    {
        return R.ok(roleService.deleteRoleByIds(roleIds));
    }

    /**
     * 获取角色选择框列表
     */
    @RequiresPermissions("system:role:query")
    @GetMapping("/optionselect")
    public R optionselect()
    {
        return R.ok(roleService.selectRoleAll());
    }
    /**
     * 查询已分配用户角色列表
     */
    @RequiresPermissions("system:role:list")
    @GetMapping("/authUser/allocatedList")
    public R<PageData<SysUser>> allocatedList(SysUser user)
    {
        PageData<SysUser> list = userService.selectAllocatedPage(user);
        return R.ok(list);
    }

    @GetMapping("/authUser/getUserByRoleKey")
    public R<List<SysUser>> getUserByRoleKey(String roleKey) {

        if(StringUtils.isBlank(roleKey)) {
            throw new BaseException("参数错误");
        }
        if("all".equals(roleKey)) {
            SysUser queryUser = new SysUser();
            queryUser.setPage(1);
            queryUser.setLimit(999);
            QueryWrapper<SysUser> baseWrapper = userService.getBaseWrapper(queryUser);
            baseWrapper.lambda().eq(SysUser::getDelFlag, 0).gt(SysUser::getUserId, 10);
            List<SysUser> list = userService.list(baseWrapper);
            return R.ok(list);
        }else {
            SysRole roleQuery = new SysRole();
            roleQuery.setRoleKey(roleKey);
            SysRole one = roleService.getOne(roleService.getBaseWrapper(roleQuery));
            if(one == null) {
                return R.ok(new ArrayList<SysUser>());
            }
            SysUser queryUser = new SysUser();
            queryUser.setPage(1);
            queryUser.setLimit(999);
            queryUser.setRoleId(one.getRoleId());
            PageData<SysUser> list = userService.selectAllocatedPage(queryUser);
            return R.ok(list.getRows());
        }
    }

    /**
     * 查询未分配用户角色列表
     */
    @RequiresPermissions("system:role:list")
    @GetMapping("/authUser/unallocatedList")
    public R<PageData<SysUser>> unallocatedList(SysUser user)
    {
        user.setStatus(UserStatus.OK.getCode());
        PageData<SysUser> list = userService.selectUnallocatedPage(user);
        return R.ok(list);
    }

    /**
     * 取消授权用户
     */
    @RequiresPermissions("system:role:edit")
    @Log(title = "角色管理", businessType = BusinessType.GRANT)
    @PutMapping("/authUser/cancel")
    public R cancelAuthUser(@RequestBody SysUserRole userRole)
    {
        return R.ok(roleService.deleteAuthUser(userRole));
    }

    /**
     * 批量取消授权用户
     */
    @RequiresPermissions("system:role:edit")
    @Log(title = "角色管理", businessType = BusinessType.GRANT)
    @PutMapping("/authUser/cancelAll")
    public R cancelAuthUserAll(Long roleId, Long[] userIds)
    {
        return R.ok(roleService.deleteAuthUsers(roleId, userIds));
    }

    /**
     * 批量选择用户授权
     */
    @RequiresPermissions("system:role:edit")
    @Log(title = "角色管理", businessType = BusinessType.GRANT)
    @PutMapping("/authUser/selectAll")
    public R selectAuthUserAll(Long roleId, Long[] userIds)
    {
        roleService.checkRoleDataScope(roleId);
        return R.ok(roleService.insertAuthUsers(roleId, userIds));
    }

    /**
     * 获取对应角色部门树列表
     */
    @RequiresPermissions("system:role:query")
    @GetMapping(value = "/deptTree/{roleId}")
    public R deptTree(@PathVariable("roleId") Long roleId)
    {
        Map ajax = new HashMap();
        ajax.put("checkedKeys", deptService.selectDeptListByRoleId(roleId));
        ajax.put("depts", deptService.selectDeptTreeList(new SysDept()));
        return R.ok(ajax);
    }

    @GetMapping("/getTgUser")
    public R<List<SysUser>> getTgUser() {
        SysRole roleQuery = new SysRole();
        roleQuery.setRoleKey("tg_push");
        SysRole one = roleService.getOne(roleService.getBaseWrapper(roleQuery));
        if(one == null) {
            return R.ok(new ArrayList<SysUser>());
        }
        SysUser queryUser = new SysUser();
        queryUser.setPage(1);
        queryUser.setLimit(999);
        queryUser.setRoleId(one.getRoleId());
        PageData<SysUser> list = userService.selectAllocatedPage(queryUser);
        return R.ok(list.getRows());
    }
}
