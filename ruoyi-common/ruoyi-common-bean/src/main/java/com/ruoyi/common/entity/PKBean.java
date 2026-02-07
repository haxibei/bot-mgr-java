package com.ruoyi.common.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.io.Serializable;

public interface PKBean<PK extends Serializable> {
    /**
     * @return 返回实体类的主键
     */
    @JsonIgnore
    PK getPK() ;

    @JsonIgnore
    default void setPK(PK pk){}
}
