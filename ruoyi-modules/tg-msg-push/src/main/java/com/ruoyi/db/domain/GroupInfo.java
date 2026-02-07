package com.ruoyi.db.domain;

import com.baomidou.mybatisplus.annotation.TableId;
import com.ruoyi.common.annotation.QueryLogic;
import com.ruoyi.common.constant.LogicEnum;
import com.ruoyi.common.entity.BaseEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author ruoyi
 * @date 2025-06-09
 */
@Data
public class GroupInfo extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** $column.columnComment */
    @TableId
    private Integer uniqueId;

    private String groupId;

    private String tgId;

    @QueryLogic(logic = LogicEnum.DefaultLike)
    private String userName;

    @QueryLogic(logic = LogicEnum.DefaultLike)
    private String groupTitle;

    private String link;

    private String remark;

    private Long deptId;

    @Override
    public Integer getPK() {
        return uniqueId;
    }

}
