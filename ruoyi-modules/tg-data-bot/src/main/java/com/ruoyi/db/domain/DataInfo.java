package com.ruoyi.db.domain;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.ruoyi.common.entity.BaseEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.math.BigDecimal;

/**
 * 充值订单对象 unique_amount
 * 
 * @author ruoyi
 * @date 2025-06-09
 */
@Data
public class DataInfo extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** $column.columnComment */
    @TableId
    private Long dataId;

    @ApiModelProperty(value = "日期")
    private String dataDate;

    @ApiModelProperty(value = "代理code")
    private String agentCode;

    @ApiModelProperty(value = "域名")
    private String domain;

    @ApiModelProperty(value = "注册")
    private Integer registerNum;

    @ApiModelProperty(value = "成效")
    private Integer effectiveNum;

    /** 有效时间起 */
    @ApiModelProperty(value = "续存")
    private Integer repeatNum;

    @TableField(exist = false)
    private Long userId;

    private String botId;

    @Override
    public Long getPK() {
        return dataId;
    }

    public int getConvertRate() {
        if(registerNum == null || registerNum == 0) {
            return 0;
        }

        return effectiveNum == null || effectiveNum == 0 ? 0 : effectiveNum * 100 / registerNum;
    }

    public int getRenewRate() {
        if(effectiveNum == null || effectiveNum == 0) {
            return 0;
        }

        return repeatNum == null || repeatNum == 0 ? 0 : repeatNum * 100 / effectiveNum;
    }
}
