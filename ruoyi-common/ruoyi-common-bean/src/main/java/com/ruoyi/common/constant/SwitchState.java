package com.ruoyi.common.constant;

public enum SwitchState implements BaseEnum {

    On("开"),

    Off("关"),
    ;

    private String descp;

    SwitchState(String descp) {
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
