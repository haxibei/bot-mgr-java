package com.ruoyi.common.constant;

/**
 * 字典表对应的 type值
 */
public enum DictKey {

    AdAgent("ad_agent", "广告代理"),
    AdCategory("ad_category", "广告账户类型"),
    AdTimezone("ad_timezone", "广告时区"),

    ;

    private String typeKey;

    private String typeName;

    private DictKey(String typeKey, String typeName) {
        this.typeKey = typeKey;
        this.typeName = typeName;
    }

    public String getTypeKey() {
        return typeKey;
    }

    public String getTypeName() {
        return typeName;
    }
}
