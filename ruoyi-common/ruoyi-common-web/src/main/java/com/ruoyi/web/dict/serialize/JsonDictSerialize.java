package com.ruoyi.web.dict.serialize;

import com.fasterxml.jackson.annotation.JacksonAnnotationsInside;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.ruoyi.common.constant.DictSpace;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})//作用于字段上
@JacksonAnnotationsInside // 表示自定义自己的注解
@JsonSerialize(using = DictContextualSerializer.class)// 该注解使用序列化的方式
public @interface JsonDictSerialize {

    DictSpace value() default DictSpace.Null;

    //获取渲染值的的字段名
    String field() default "";

    //渲染输出的字段名
    String serializeName() default "";

    //拼在 需要渲染 值的 前缀 eg: 当前缀为 sys_user_sex: , 值为 0 时, 则会 选择 sys_user_sex:0 作为值去做解析
    String prefix() default "";

    boolean multi() default false;
}