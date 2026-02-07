package com.ruoyi.model;

import lombok.Data;

import java.util.List;

@Data
public class RemoteValue {

    private List<RemoteRowData> list;

    private RemoteRowData footer;

    private Integer total;

    private Integer totalPages;
}
