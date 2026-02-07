package com.ruoyi.common.constant;

public enum SystemRoleKey {

    AdOperator("ad_operator", "广告运营"),
    AdFinance("ad_finance", "广告财务"),
    ;
    private String roleKey;

    private String descp;

    SystemRoleKey(String roleKey, String descp) {
        this.roleKey = roleKey;
        this.descp = descp;
    }

    public String getRoleKey() {
        return roleKey;
    }

    public String getDescp() {
        return descp;
    }
}
