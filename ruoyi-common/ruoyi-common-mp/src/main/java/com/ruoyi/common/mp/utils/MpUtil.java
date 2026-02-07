package com.ruoyi.common.mp.utils;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.enums.SqlKeyword;
import com.baomidou.mybatisplus.core.toolkit.StringPool;
import com.ruoyi.common.core.utils.bean.BeanUtils;
import com.ruoyi.common.mp.model.BeanQueryModel;
import com.ruoyi.common.mp.model.CustQueryModel;
import com.ruoyi.common.annotation.QueryLogic;
import com.ruoyi.common.core.utils.StringUtils;
import com.ruoyi.common.core.web.domain.IScopeEntity;
import com.ruoyi.common.entity.BaseEntity;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.formula.functions.T;

import java.lang.reflect.Field;
import java.util.*;

@Slf4j
public class MpUtil {

    public static void setBaseWrapper(QueryWrapper queryWrapper, Object t) {
        Map<String, Object> beanMap = BeanUtils.beanToMap(t);

        if (beanMap != null) {
            Field[] currfFields = t.getClass().getDeclaredFields();
            List<String> fieldNames = new ArrayList<>();
            for (Field currfField : currfFields) {
                fieldNames.add(currfField.getName());
            }
            List<Field> fields = new ArrayList<>();
            fields.addAll(Arrays.asList(currfFields));

            Field[] superFields = t.getClass().getSuperclass().getDeclaredFields();
            for (Field superField : superFields) {
                if(!fieldNames.contains(superField.getName())) {
                    fields.add(superField);
                }
            }

            if (fields.size() > 0) {
                List<CustQueryModel> queryFields = new ArrayList<>();
                List<BeanQueryModel> beanFields = new ArrayList<>();
                for (Field field : fields) {
                    String fieldName = StringUtils.lowerFirst(field.getName());
                    Object val = beanMap.get(fieldName);

                    if(val == null || !isBaseType(val) || specialField(fieldName)) {//为空或者是非基本类型
                        continue;
                    }

                    QueryLogic queryAnno = field.getAnnotation(QueryLogic.class);
                    if(queryAnno != null) {
                        queryFields.add(new CustQueryModel(queryAnno, fieldName, val));
                        setNull(field, t);
                    }else {
                        TableField annotation = field.getAnnotation(TableField.class);
                        if (annotation == null || annotation.exist()) {
                            beanFields.add(new BeanQueryModel(fieldName, val.toString()));
                        }
                    }
                }

                dealCustQuery(queryWrapper, queryFields);
                dealBeanQuery(queryWrapper, beanFields);
            }
        }
        if(t instanceof IScopeEntity) {
            String dataScope = ((IScopeEntity) t).getDataScope();
            if(StringUtils.isNotBlank(dataScope)) {
                if(dataScope.startsWith(" AND")) {
                    dataScope = dataScope.replaceFirst(" AND", "");
                }
                queryWrapper.apply(dataScope);
            }
        }
        if(t instanceof BaseEntity) {
            buildOrderSql(queryWrapper, ((BaseEntity) t).getOrdseg());
        }
    }

    private static void setNull(Field field, Object t) {
        try {
            field.setAccessible(true);
            field.set(t, null);
        } catch (IllegalAccessException e) {
            log.error("设置属性为【null】失败", e);
        }
    }

    //特殊字段不做查询
    private static boolean specialField(String fieldName) {
        if("dataScope".equals(fieldName)) {
            return true;
        }
        return false;
    }

    private static void dealBeanQuery(QueryWrapper queryWrapper, List<BeanQueryModel> fields) {
        for (BeanQueryModel model : fields) {
            String field = model.getField();
            String fieldVal = model.getVal();

            String fieldName = StringUtils.cameltoUnderline(field);
            queryWrapper.eq(fieldName, fieldVal);
        }
    }

    private static void dealCustQuery(QueryWrapper queryWrapper, List<CustQueryModel> fields) {

        for (CustQueryModel model : fields) {
            QueryLogic logic = model.getLogic();

            String field = model.getField();
            Object fieldVal = model.getVal();
            if(fieldVal instanceof String) {
                fieldVal = fieldVal.toString().trim();
            }

            String fieldName = StringUtils.cameltoUnderline(field);
            if(StringUtils.isNotBlank(logic.field())) {
                fieldName = logic.field();
            }

            switch (logic.logic()) {
                case Eq: queryWrapper.eq(fieldName, fieldVal);
                    break;
                case Lt: queryWrapper.lt(fieldName, fieldVal);
                    break;
                case Gt: queryWrapper.gt(fieldName, fieldVal);
                    break;
                case Le: queryWrapper.le(fieldName, fieldVal);
                    break;
                case Ge: queryWrapper.ge(fieldName, fieldVal);
                    break;
                case DefaultLike: queryWrapper.like(fieldName, fieldVal);
                    break;
                case LeftLike: queryWrapper.likeLeft(fieldName, fieldVal);
                    break;
                case RightLike: queryWrapper.likeRight(fieldName, fieldVal);
                    break;
            }
        }
    }

    /**
     * @param queryWrapper
     * @param ordseg       形如  "id.desc, age.asc"
     */
    private static void buildOrderSql(QueryWrapper<T> queryWrapper, String ordseg) {

        if (queryWrapper == null || StringUtils.isBlank(ordseg)) {
            return;
        }

        String[] ords = ordseg.split(",");

        for (String ord : ords) {
            ord = ord.trim();
            if (StringUtils.isNotBlank(ord)) {

                String[] ordStr = ord.split(StringPool.BACK_SLASH + StringPool.DOT);

                if (ordStr.length == 1) {
                    queryWrapper.orderByAsc(StringUtils.cameltoUnderline(ordStr[0]));
                } else if (ordStr.length == 2) {
                    String field = StringUtils.cameltoUnderline(ordStr[0]);
                    if (SqlKeyword.ASC.name().toLowerCase().equals(ordStr[1])) {
                        queryWrapper.orderByAsc(field);
                    } else if (SqlKeyword.DESC.name().toLowerCase().equals(ordStr[1])) {
                        queryWrapper.orderByDesc(field);
                    }
                }
            }
        }

    }

    public static void main(String[] args) {
        String xx = "111";
        System.out.println(xx.replaceFirst("1", ""));
    }

    /**
     *
     * @return
     */
    private static boolean isBaseType(Object obj) {
        if(obj instanceof String) {
            return true;
        }
        Class<?> clazz = obj.getClass();
        if(clazz.isPrimitive() || clazz.isEnum()) {
            return true;
        }else if(obj instanceof Character
                || obj instanceof Boolean
                || obj instanceof Byte
                || obj instanceof Short
                || obj instanceof Integer
                || obj instanceof Long
                || obj instanceof Double
                || obj instanceof Float
                || obj instanceof Double
        ) {
            return true;
        }

        return false;
    }
}
