package com.ruoyi.db.domain;

import com.ruoyi.common.entity.BaseEntity;
import io.swagger.annotations.ApiModelProperty;

import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * 用户对象 commandusers
 * 
 * @author ruoyi
 * @date 2025-06-09
 */
@Data
public class Commandusers extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** $column.columnComment */
    /** $column.columnComment */
    @TableId
    private Long userId;

    /** 用户昵称 */
    @ApiModelProperty(value = "用户昵称")
    private String userName;

    /** 用户昵称 */
    @ApiModelProperty(value = "用户昵称")
    private String nickName;

    /** 推荐人id */
    @ApiModelProperty(value = "推荐人id")
    private Long recommendUid;

    /** $column.columnComment */
    @ApiModelProperty(value = "${comment}")
    private Long status;

    private String botId;

    @Override
    public Long getPK() {
        return userId;
    }
    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("userId", getUserId())
            .append("nickName", getNickName())
            .append("recommendUid", getRecommendUid())
            .append("status", getStatus())
            .append("createTime", getCreateTime())
            .toString();
    }
}
