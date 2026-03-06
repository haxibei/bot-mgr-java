package com.ruoyi.db.domain;

import io.swagger.annotations.ApiModelProperty;

import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import com.ruoyi.common.entity.BaseEntity;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * 机器人信息对象 bot_info
 * 
 * @author ruoyi
 * @date 2026-02-21
 */
@Data
public class BotInfo extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** $column.columnComment */
    @TableId
    private Long botId;

    /** token */
    @ApiModelProperty(value = "token")
    private String botToken;

    /** 机器人用户名 */
    @ApiModelProperty(value = "机器人用户名")
    private String botUser;

    @Override
    public Long getPK() {
        return botId;
    }
    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("botId", getBotId())
            .append("botToken", getBotToken())
            .append("botUser", getBotUser())
            .append("delFlag", getDelFlag())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .toString();
    }
}
