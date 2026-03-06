package com.ruoyi.db.domain;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.ruoyi.common.entity.BaseEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * 机器人信息对象 bot_info
 * 
 * @author ruoyi
 * @date 2026-02-21
 */
@Data
public class QueryLog extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** $column.columnComment */
    @TableId
    private Long logId;

    /** token */
    @ApiModelProperty(value = "tgId")
    private String tgId;

    @ApiModelProperty(value = "tg用户名")
    private String tgUser;

    /** 机器人用户名 */
    @ApiModelProperty(value = "tg昵称")
    private String tgName;

    private String domain;

    private String agent;

    private String date;

    private String queryResult;

    private String botId;

    @Override
    public Long getPK() {
        return logId;
    }

    @TableField(exist = false)
    private String inBlack = "0";
}
