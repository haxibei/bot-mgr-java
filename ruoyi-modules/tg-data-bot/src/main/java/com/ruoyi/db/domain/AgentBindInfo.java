package com.ruoyi.db.domain;

import com.baomidou.mybatisplus.annotation.TableId;
import com.ruoyi.common.entity.BaseEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author ruoyi
 * @date 2025-06-09
 */
@Data
public class AgentBindInfo extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** $column.columnComment */
    @TableId
    private Long bindId;

    @ApiModelProperty(value = "用户Id")
    private Long userId;

    @ApiModelProperty(value = "代理code")
    private String agentCode;

    @ApiModelProperty(value = "域名")
    private String domain;

    private String botId;

    @ApiModelProperty(value = "代理层级    默认  0")
    private Integer level;

    private String parentAgentCode;

    @Override
    public Long getPK() {
        return bindId;
    }

}
