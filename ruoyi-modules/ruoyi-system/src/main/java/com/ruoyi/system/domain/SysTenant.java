package com.ruoyi.system.domain;

import io.swagger.annotations.ApiModelProperty;

import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import com.ruoyi.common.entity.BaseEntity;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * 租户信息对象 sys_tenant
 * 
 * @author ruoyi
 * @date 2025-04-20
 */
@Data
public class SysTenant extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 租户ID */
    @TableId
    private Long tenantId;

    /** 租户名称 */
    @ApiModelProperty(value = "租户名称")
    private String tenantName;

    /** 备注 */
    @ApiModelProperty(value = "备注")
    private String remark;

    @Override
    public Long getPK() {
        return tenantId;
    }
    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("tenantId", getTenantId())
            .append("tenantName", getTenantName())
            .append("delFlag", getDelFlag())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .append("remark", getRemark())
            .toString();
    }
}
