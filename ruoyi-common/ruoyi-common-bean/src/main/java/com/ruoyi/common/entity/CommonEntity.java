package com.ruoyi.common.entity;

import com.alibaba.fastjson2.annotation.JSONField;
import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonIgnore;

import com.ruoyi.common.core.web.domain.IScopeEntity;
import com.ruoyi.common.core.web.domain.PageInfo;
import lombok.Data;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Entity基类
 * 
 * @author ruoyi
 */
@Data
public abstract class CommonEntity<PK extends Serializable> implements Serializable, PKBean<PK>, IScopeEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 接受分页参数
     */
    @JsonIgnore
    @JSONField(serialize = false)
    @TableField(exist = false)
    protected PageInfo pageInfo; //此处修饰为 transient 主要是为了不让Gson序列化此值

    /** 模糊搜索值 */
    @JsonIgnore
    @TableField(exist = false)
    private String searchValue;

    @JsonIgnore
    @JSONField(serialize = false)
    @TableField(exist = false)
    private Map<String, Object> queryParams;

    @JsonIgnore
    public Map<String, Object> getQueryParams() {
        return queryParams == null?new HashMap<>() :queryParams;
    }

    @JsonIgnore
    public Object getQueryParam(String key) {
        return queryParams == null? null : queryParams.get(key);
    }

    @TableField(exist = false)
    @JsonIgnore
    @JSONField(serialize = false)
    protected String ordseg;

    @TableField(exist = false)
    protected String dataScope;

    public void setPage(int page) {
        if (pageInfo == null) {
            pageInfo = new PageInfo(page, -1);//不使用limit
        } else {
            pageInfo.setPage(page);
        }
    }

    public void setLimit(int limit) {
        if (pageInfo == null) {
            pageInfo = new PageInfo(0, limit);
        } else {
            pageInfo.setLimit(limit);
        }
    }

    public void setPageNum(int pageNum) {
        if (pageInfo == null) {
            pageInfo = new PageInfo(pageNum, -1);//不使用limit
        } else {
            pageInfo.setPage(pageNum);
        }
    }

    public void setPageSize(int pageSize) {
        if (pageInfo == null) {
            pageInfo = new PageInfo(0, pageSize);
        } else {
            pageInfo.setLimit(pageSize);
        }
    }

}
