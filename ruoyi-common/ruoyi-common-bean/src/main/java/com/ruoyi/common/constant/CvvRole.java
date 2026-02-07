package com.ruoyi.common.constant;

public enum CvvRole implements BaseEnum{
    Administrator("管理员"),
    Finance("财务"),
    Operator("运营")
    ;

    private String descp;

    CvvRole(String descp) {
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
