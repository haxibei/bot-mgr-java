package com.ruoyi.db.domain;

import com.baomidou.mybatisplus.annotation.TableId;
import com.ruoyi.common.entity.BaseEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 充值订单对象 unique_amount
 * 
 * @author ruoyi
 * @date 2025-06-09
 */
@Data
public class DomainInfo extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** $column.columnComment */
    @TableId
    private Long domainId;

    /** 打款金额 整数部分 */
    @ApiModelProperty(value = "域名")
    private String domain;

    @ApiModelProperty(value = "命令")
    private String command;

    /** 打款金额 小数金额 */
    @ApiModelProperty(value = "请求地址 %s 为参数 日期")
    private String reqUrl;

    @ApiModelProperty(value = "请求头部")
    private String header;

    @ApiModelProperty(value = "所属机器人id")
    private String botId;

    @ApiModelProperty(value = "是否需要查询 一级代理的数据 1 是  0  否")
    private Integer needAgentLevel1;

    @ApiModelProperty(value = "当前代理数据请求地址")
    private String reqLv1Url1;

    @ApiModelProperty(value = "下级代理数据请求地址")
    private String reqLv1Url2;

    @ApiModelProperty(value = "留存数据请求地址")
    private String reqLv1Url3;

    private String merchantCode;

    @Override
    public Long getPK() {
        return domainId;
    }

}
