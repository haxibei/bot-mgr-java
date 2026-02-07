package com.ruoyi.common.entity;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class PageData<E> implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = -8358366962449176827L;

    /** 总记录数 */
    private long total;

    /** 列表数据 */
    private List<E> rows;

    private long totalPages;

    private long page;

    private long limit;

    public PageData() {
        rows = null;
    }

    public PageData(List<E> datas, long currPage, long maxRows, long totalCount, long totalPages) {
        this.rows = datas;
        this.page = currPage;
        this.limit = maxRows;
        this.total = totalCount;
        this.totalPages = totalPages;
    }
}
