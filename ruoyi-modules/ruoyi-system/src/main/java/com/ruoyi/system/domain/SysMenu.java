package com.ruoyi.system.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.ruoyi.common.entity.BaseEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * 菜单权限表 sys_menu
 * 
 * @author ruoyi
 */
@Data
public class SysMenu extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 菜单ID */
    @TableId
    private Long menuId;

    /** 菜单名称 */
    @NotBlank(message = "菜单名称不能为空")
    @Size(min = 0, max = 50, message = "菜单名称长度不能超过50个字符")
    private String menuName;

    /** 父菜单名称 */
    private String parentName;

    /** 父菜单ID */
    private Long parentId;

    /** 显示顺序 */
    @NotNull(message = "显示顺序不能为空")
    private Integer orderNum;

    /** 路由地址 */
    @Size(min = 0, max = 200, message = "路由地址不能超过200个字符")
    private String path;

    /** 组件路径 */
    @Size(min = 0, max = 200, message = "组件路径不能超过255个字符")
    private String component;

    /** 路由参数 */
    private String query;

    /** 是否为外链（0是 1否） */
    private String isFrame;

    /** 是否缓存（0缓存 1不缓存） */
    private String isCache;

    /** 类型（M目录 C菜单 F按钮） */
    @NotBlank(message = "菜单类型不能为空")
    private String menuType;

    /** 显示状态（0显示 1隐藏） */
    private String visible;

    /** 是否用于小程序 1 用于小程序 0 不用于小程序 */
    private String useInMa;

    /** 菜单状态（0正常 1停用） */
    private String status;

    /** 权限字符串 */
    @Size(min = 0, max = 100, message = "权限标识长度不能超过100个字符")
    private String perms;

    /** 菜单图标 */
    private String icon;

    @ApiModelProperty("待办统计key")
    private String statKey;

    @TableField(exist = false)
    @ApiModelProperty("待办统计")
    private Integer todoCnt;

    /** 子菜单 */
    @TableField(exist = false)
    private List<SysMenu> children = new ArrayList<SysMenu>();



    private String remark;


    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
                .append("menuId", getMenuId())
                .append("menuName", getMenuName())
                .append("parentId", getParentId())
                .append("orderNum", getOrderNum())
                .append("path", getPath())
                .append("component", getComponent())
                .append("isFrame", getIsFrame())
                .append("IsCache", getIsCache())
                .append("menuType", getMenuType())
                .append("visible", getVisible())
                .append("status ", getStatus())
                .append("perms", getPerms())
                .append("icon", getIcon())
                .append("createBy", getCreateBy())
                .append("createTime", getCreateTime())
                .append("updateBy", getUpdateBy())
                .append("updateTime", getUpdateTime())
                .append("remark", getRemark())
                .toString();
    }

    @Override
    public Long getPK() {
        return menuId;
    }
}
