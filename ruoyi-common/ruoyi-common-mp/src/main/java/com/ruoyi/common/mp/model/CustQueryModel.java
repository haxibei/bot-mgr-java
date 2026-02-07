package com.ruoyi.common.mp.model;

import com.ruoyi.common.annotation.QueryLogic;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.lang.reflect.Field;

@Data
@AllArgsConstructor
public class CustQueryModel {

    private QueryLogic logic;

    private String field;

    private Object val;

}
