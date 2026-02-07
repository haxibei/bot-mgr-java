package com.ruoyi.common.constant;

public enum PutOnState implements BaseEnum {

    None("未上架"),
    On("已上架"),

    Off("已上架");

    private String descp;

    PutOnState(String descp) {
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
