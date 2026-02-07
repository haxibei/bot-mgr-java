package com.ruoyi.common.entity;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

@Data
public class DictMap implements Serializable {

    private String key;

    private String value;

    private Map<String, String> extend;

    public DictMap(String key, String value) {
        this.key = key;
        this.value = value;

        this.extend = new HashMap<>();
    }

    public void addExtend(String key, String value) {
        this.extend.put(key, value);
    }
}
