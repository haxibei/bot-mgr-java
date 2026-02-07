package com.ruoyi.model;

import lombok.Data;

@Data
public class RemoteResp {

    private Boolean success;

    private RemoteValue value;

    private String errorCode;

    public boolean invalidTk() {
        return "INVALID_TOKEN".equals(errorCode);
    }
}
