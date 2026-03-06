package com.ruoyi.model;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class RemoteConvertData {

    @JsonAlias({"sameDayRetentionCount"})
    private Integer retentionCount;

    @JsonAlias({"sameDayRetentionRate"})
    private BigDecimal retentionRate;
}
