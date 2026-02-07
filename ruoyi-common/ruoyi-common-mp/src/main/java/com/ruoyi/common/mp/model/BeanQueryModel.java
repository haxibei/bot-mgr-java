package com.ruoyi.common.mp.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.lang.reflect.Field;

@Data
@AllArgsConstructor
public class BeanQueryModel {

    private String field;

    private String val;

}
