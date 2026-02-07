package com.ruoyi.db.domain;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.ruoyi.common.constant.DictSpace;
import com.ruoyi.common.entity.BaseEntity;
import com.ruoyi.constant.TgType;
import com.ruoyi.web.dict.serialize.JsonDictSerialize;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 充值订单对象 unique_amount
 * 
 * @author ruoyi
 * @date 2025-06-09
 */
@Data
public class TgInfo extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    @TableId
    private String tgId;

    private String userName;

    private String link;

    private String remark;

    private String apiId;

    private String apiHash;

    @JsonDictSerialize(value = DictSpace.SysUserInfo, field = "nick_name", serializeName = "ownerName")
    private Long ownerUid;

    private TgType tgType;

    private String botToken;

    private Long deptId;

    @Override
    public String getPK() {
        return tgId;
    }

}
