package com.ruoyi.model;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class RemoteRowData {

    private String agentName;

    @JsonAlias({"teamRegisterMemberCount"})
    private Integer registerCount;

    @JsonAlias({"teamDepositMemberCount"})
    private Integer depositCount;

    private Integer deposit2Count;

    @JsonAlias({"teamFirstDepositMemberCount"})
    private Integer firstDepositCount;

}
