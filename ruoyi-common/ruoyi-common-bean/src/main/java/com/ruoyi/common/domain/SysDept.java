package com.ruoyi.common.domain;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.ruoyi.common.entity.BaseEntity;
import lombok.Data;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 部门表 sys_dept
 * 
 * @author ruoyi
 */
@Data
public class SysDept extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 部门ID */
    @TableId
    private Long deptId;

    /** 父部门ID */
    private Long parentId;

    /** 祖级列表 */
    private String ancestors;

    /** 部门名称 */
    @NotBlank(message = "部门名称不能为空")
    @Size(min = 0, max = 30, message = "部门名称长度不能超过30个字符")
    private String deptName;

    /** 显示顺序 */
    @NotNull(message = "显示顺序不能为空")
    private Integer orderNum;

    /** 负责人 */
    private String leader;

    /** 联系电话 */
    @Size(min = 0, max = 11, message = "联系电话长度不能超过11个字符")
    private String phone;

    /** 邮箱 */
    @Email(message = "邮箱格式不正确")
    @Size(min = 0, max = 50, message = "邮箱长度不能超过50个字符")
    private String email;

    /** 部门状态:0正常,1停用 */
    private String status;

    /** 父部门名称 */
    @TableField(exist = false)
    private String parentName;
    
    /** 子部门 */
    @TableField(exist = false)
    private List<SysDept> children = new ArrayList<SysDept>();

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("deptId", getDeptId())
            .append("parentId", getParentId())
            .append("ancestors", getAncestors())
            .append("deptName", getDeptName())
            .append("orderNum", getOrderNum())
            .append("leader", getLeader())
            .append("phone", getPhone())
            .append("email", getEmail())
            .append("status", getStatus())
            .append("delFlag", getDelFlag())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .toString();
    }

    @Override
    public Long getPK() {
        return deptId;
    }
}
