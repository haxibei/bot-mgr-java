package com.ruoyi.constant;

import com.baomidou.mybatisplus.annotation.IEnum;
import com.ruoyi.common.constant.BaseEnum;

import java.io.Serializable;

public enum TgType implements BaseEnum {

    bot,
    account
    ;

    @Override
    public String getValue() {
        return this.name();
    }

    @Override
    public String getDescp() {
        return "";
    }
}
