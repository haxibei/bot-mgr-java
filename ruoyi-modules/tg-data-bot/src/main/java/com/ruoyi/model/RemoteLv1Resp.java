package com.ruoyi.model;

import lombok.Data;

@Data
public class RemoteLv1Resp {

    private Boolean success;

    private RemoteRowData value;

    private String errorCode;

    public boolean invalidTk() {
        return "INVALID_TOKEN".equals(errorCode);
    }
}
