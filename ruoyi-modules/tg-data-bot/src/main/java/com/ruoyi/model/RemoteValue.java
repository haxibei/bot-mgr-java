package com.ruoyi.model;

import lombok.Data;

import java.util.List;

@Data
public class RemoteValue<T> {

    private List<T> list;

    private RemoteRowData footer;

    private Integer total;

    private Integer totalPages;
}
