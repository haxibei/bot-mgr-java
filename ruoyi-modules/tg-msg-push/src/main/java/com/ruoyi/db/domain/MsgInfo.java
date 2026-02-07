package com.ruoyi.db.domain;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.ruoyi.common.annotation.QueryLogic;
import com.ruoyi.common.constant.LogicEnum;
import com.ruoyi.common.core.annotation.Excel;
import com.ruoyi.common.core.constant.ScheduleConstants;
import com.ruoyi.common.core.utils.StringUtils;
import com.ruoyi.common.entity.BaseEntity;
import com.ruoyi.quartz.entity.IJob;
import io.swagger.annotations.ApiModelProperty;

import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.io.Serializable;

/**
 * 用户对象 commandusers
 * 
 * @author ruoyi
 * @date 2025-06-09
 */
@Data
public class MsgInfo extends BaseEntity implements IJob
{
    private static final long serialVersionUID = 1L;

    @TableId
    private Long msgId;

    private String tgId;

    private String tgName;

    private String content;

    private String status;

    private String cronExpression;

    /** 用户昵称 */
    private String chatIds;

    @QueryLogic(logic = LogicEnum.DefaultLike)
    private String remark;

    private String invokeTarget;

    /** 是否并发执行（0允许 1禁止） */
    private String concurrent;

    private String imgList;

    private String videoList;

    @TableField(fill = FieldFill.INSERT)
    private Long deptId;

    @Override
    public Long getPK() {
        return msgId;
    }

    @Override
    public Long getJobId() {
        return msgId;
    }

    @Override
    public String getJobName() {
        return getRemark();
    }

    @Override
    public String getJobGroup() {
        return "msg_push";
    }

    @Override
    public String getInvokeTarget() {
        return StringUtils.isNotBlank(invokeTarget)?invokeTarget:String.format("msgTask.push('%s')", msgId);
    }

    @Override
    public String getMisfirePolicy() {
        return "3";//放弃执行
    }

    @Override
    public String getConcurrent() {
        return StringUtils.isNotBlank(concurrent)?concurrent:"0";//默认开启并发
    }
}
