package com.ruoyi.db.domain;

import com.baomidou.mybatisplus.annotation.TableId;
import com.ruoyi.common.entity.BaseEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 机器人信息对象 bot_info
 * 
 * @author ruoyi
 * @date 2026-02-21
 */
@Data
public class QueryBlack extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    @TableId
    private Long blackId;

    /** token */
    @ApiModelProperty(value = "tgId")
    private String tgId;

    @ApiModelProperty(value = "机器人Id")
    private String botId;

    @Override
    public Long getPK() {
        return blackId;
    }

}
