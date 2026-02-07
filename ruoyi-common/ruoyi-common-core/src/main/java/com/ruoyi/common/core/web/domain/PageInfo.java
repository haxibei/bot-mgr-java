package com.ruoyi.common.core.web.domain;

import lombok.Data;

import java.io.Serializable;

@Data
public class PageInfo implements Serializable {

    /**
     * 页号
     */
    private int page = 1;
    /**
     * 分页大小
     */
    private int limit = 10;

    public PageInfo() {

    }

    public PageInfo(int page, int limit) {
        this.page = page;
        this.limit = limit;
    }

    public void setPage(int page) {
        this.page = page < 1?1:page;
    }

    public void setLimit(int limit) {
        this.limit = limit < 1?12:limit;
    }

    public void setPageNum(int pageNum) {
        this.page = pageNum < 1?1:pageNum;
    }

    public void setPageSize(int pageSize) {
        this.limit = pageSize < 1?12:pageSize;
    }
}
