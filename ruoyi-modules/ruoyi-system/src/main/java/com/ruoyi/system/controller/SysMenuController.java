package com.ruoyi.system.controller;

import com.ruoyi.common.core.constant.UserConstants;
import com.ruoyi.common.core.domain.R;
import com.ruoyi.common.core.utils.StringUtils;
import com.ruoyi.common.log.annotation.Log;
import com.ruoyi.common.log.enums.BusinessType;
import com.ruoyi.common.security.annotation.RequiresPermissions;
import com.ruoyi.common.security.utils.SecurityUtils;
import com.ruoyi.system.domain.SysMenu;
import com.ruoyi.system.domain.vo.RouterVo;
import com.ruoyi.system.domain.vo.TreeSelect;
import com.ruoyi.system.param.BatchSaveMenuParam;
import com.ruoyi.system.service.ISysMenuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 菜单信息
 * 
 * @author ruoyi
 */
@RestController
@RequestMapping("/menu")
public class SysMenuController
{
    @Autowired
    private ISysMenuService menuService;

    /**
     * 获取菜单列表
     */
    @RequiresPermissions("system:menu:list")
    @GetMapping("/list")
    public R<List<SysMenu>> list(SysMenu menu)
    {
        Long userId = 1L;//SecurityUtils.getUserId(); 默认给全部菜单
        List<SysMenu> menus = menuService.selectMenuList(menu, userId);
        return R.ok(menus);
    }

    /**
     * 根据菜单编号获取详细信息
     */
    @RequiresPermissions("system:menu:query")
    @GetMapping(value = "/{menuId}")
    public R<SysMenu> getInfo(@PathVariable Long menuId)
    {
        return R.ok(menuService.selectMenuById(menuId));
    }

    /**
     * 获取菜单下拉树列表
     */
    @GetMapping("/treeselect")
        public R<List<TreeSelect>> treeselect(SysMenu menu)
    {
        Long userId = 1L;//SecurityUtils.getUserId();
        List<SysMenu> menus = menuService.selectMenuList(menu, userId);
        return R.ok(menuService.buildMenuTreeSelect(menus));
    }

    /**
     * 加载对应角色菜单列表树
     */
    @GetMapping(value = "/roleMenuTreeselect/{roleId}")
    public R roleMenuTreeselect(@PathVariable("roleId") Long roleId)
    {
        Long userId = 1L;//SecurityUtils.getUserId();
        List<SysMenu> menus = menuService.selectMenuList(userId);
        Map ajax = new HashMap<>();
        ajax.put("checkedKeys", menuService.selectMenuListByRoleId(roleId));
        ajax.put("menus", menuService.buildMenuTreeSelect(menus));
        return R.ok(ajax);
    }

    @RequiresPermissions("system:menu:add")
    @Log(title = "菜单批量保存", businessType = BusinessType.INSERT)
    @PostMapping("batchDoSave")
    public R batchDoSave(@Validated @RequestBody BatchSaveMenuParam param)
    {
        for(SysMenu menu : param.getMenus()) {
            if(menu.getMenuId() != null) {//更新
                if (!menuService.checkMenuNameUnique(menu))
                {
                    return R.fail("修改菜单'" + menu.getMenuName() + "'失败，菜单名称已存在");
                }
                else if (UserConstants.YES_FRAME.equals(menu.getIsFrame()) && !StringUtils.ishttp(menu.getPath()))
                {
                    return R.fail("修改菜单'" + menu.getMenuName() + "'失败，地址必须以http(s)://开头");
                }
                else if (menu.getMenuId().equals(menu.getParentId()))
                {
                    return R.fail("修改菜单'" + menu.getMenuName() + "'失败，上级菜单不能选择自己");
                }else if (!menuService.checkMenuPathUnique(menu))
                {
                    return R.fail("新增菜单'" + menu.getMenuName() + "'失败，path: "+ menu.getPath() +"已存在");
                }
                menuService.updateMenu(menu);
            }else {
                if (!menuService.checkMenuNameUnique(menu))
                {
                    return R.fail("新增菜单'" + menu.getMenuName() + "'失败，菜单名称已存在");
                }else if (!menuService.checkMenuPathUnique(menu))
                {
                    return R.fail("新增菜单'" + menu.getMenuName() + "'失败，path: "+ menu.getPath() +"已存在");
                }
                else if (UserConstants.YES_FRAME.equals(menu.getIsFrame()) && !StringUtils.ishttp(menu.getPath()))
                {
                    return R.fail("新增菜单'" + menu.getMenuName() + "'失败，地址必须以http(s)://开头");
                }

                menuService.insertMenu(menu);
            }
        }
        return R.ok();
    }

    /**
     * 新增菜单
     */
    @RequiresPermissions("system:menu:add")
    @Log(title = "菜单管理", businessType = BusinessType.INSERT)
    @PostMapping
    public R add(@Validated @RequestBody SysMenu menu)
    {
        if (!menuService.checkMenuNameUnique(menu))
        {
            return R.fail("新增菜单'" + menu.getMenuName() + "'失败，菜单名称已存在");
        }
        else if (UserConstants.YES_FRAME.equals(menu.getIsFrame()) && !StringUtils.ishttp(menu.getPath()))
        {
            return R.fail("新增菜单'" + menu.getMenuName() + "'失败，地址必须以http(s)://开头");
        }else if (!menuService.checkMenuPathUnique(menu))
        {
            return R.fail("新增菜单'" + menu.getMenuName() + "'失败，path: "+ menu.getPath() +"已存在");
        }
        return R.ok(menuService.insertMenu(menu));
    }

    /**
     * 修改菜单
     */
    @RequiresPermissions("system:menu:edit")
    @Log(title = "菜单管理", businessType = BusinessType.UPDATE)
    @PutMapping
    public R edit(@Validated @RequestBody SysMenu menu)
    {
        if (!menuService.checkMenuNameUnique(menu))
        {
            return R.fail("修改菜单'" + menu.getMenuName() + "'失败，菜单名称已存在");
        }
        else if (UserConstants.YES_FRAME.equals(menu.getIsFrame()) && !StringUtils.ishttp(menu.getPath()))
        {
            return R.fail("修改菜单'" + menu.getMenuName() + "'失败，地址必须以http(s)://开头");
        }
        else if (menu.getMenuId().equals(menu.getParentId()))
        {
            return R.fail("修改菜单'" + menu.getMenuName() + "'失败，上级菜单不能选择自己");
        }else if (!menuService.checkMenuPathUnique(menu))
        {
            return R.fail("新增菜单'" + menu.getMenuName() + "'失败，path: "+ menu.getPath() +"已存在");
        }
        return R.ok(menuService.updateMenu(menu));
    }

    /**
     * 删除菜单
     */
    @RequiresPermissions("system:menu:remove")
    @Log(title = "菜单管理", businessType = BusinessType.DELETE)
    @DeleteMapping("/{menuId}")
    public R remove(@PathVariable("menuId") Long menuId)
    {
        if (menuService.hasChildByMenuId(menuId))
        {
            return R.fail("存在子菜单,不允许删除");
        }
//        if (menuService.checkMenuExistRole(menuId))
//        {
//            return R.fail("菜单已分配,不允许删除");
//        }
        return R.ok(menuService.deleteMenuById(menuId));
    }

    /**
     * 获取路由信息
     * 
     * @return 路由信息
     */
    @GetMapping("getRouters")
    public R getRouters()
    {
        Long userId = SecurityUtils.getUserId();
        List<SysMenu> menus = menuService.selectMenuTreeByUserId(userId);

        List<RouterVo> routerVos = menuService.buildMenus(menus);
        return R.ok(routerVos);
    }
}