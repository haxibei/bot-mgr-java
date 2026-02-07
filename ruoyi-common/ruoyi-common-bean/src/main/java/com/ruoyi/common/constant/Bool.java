package com.ruoyi.common.constant;

public enum Bool implements BaseEnum {

    Y("是"),
    N("否"),

    ;

    private String descp;

    Bool(String descp) {
        this.descp = descp;
    }

    @Override
    public String getDescp() {
        return descp;
    }

    @Override
    public String getValue() {
        return this.name();
    }
}
