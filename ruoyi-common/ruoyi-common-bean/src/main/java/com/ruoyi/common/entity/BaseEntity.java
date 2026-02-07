package com.ruoyi.common.entity;

import com.alibaba.fastjson2.annotation.JSONField;
import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Entity基类
 * 
 * @author ruoyi
 */
@Data
public abstract class BaseEntity<PK extends Serializable> extends CommonEntity<PK> {

    private static final long serialVersionUID = 1L;



    /** 创建者 */
    @TableField(fill = FieldFill.INSERT)
    private Long createBy;

    /** 创建时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /** 更新者 */
    @TableField(fill = FieldFill.UPDATE)
    @JsonIgnore
    @JSONField(serialize = false)
    private Long updateBy;

    /** 更新时间 */

    @TableField(fill = FieldFill.UPDATE)
    @JsonIgnore
    @JSONField(serialize = false)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;

    @JsonIgnore
    @JSONField(serialize = false)
    private long delFlag;
}
