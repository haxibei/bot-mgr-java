package com.ruoyi.common.annotation;

import com.ruoyi.common.constant.LogicEnum;

import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface QueryLogic {
    LogicEnum logic() default LogicEnum.Eq;

    String field() default "";
}
