package com.ruoyi.web.dict.serialize;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.ContextualSerializer;
import com.ruoyi.common.core.utils.StringUtils;

import java.io.IOException;
import java.util.Objects;

public class DictContextualSerializer extends JsonSerializer implements ContextualSerializer {

    @Override
    public void serialize(Object o, JsonGenerator gen, SerializerProvider provider) throws IOException {

    }

    @Override
    public JsonSerializer<?> createContextual(SerializerProvider provider, BeanProperty beanProperty) throws JsonMappingException {
        if (beanProperty != null) {
            Class<?> clazz = beanProperty.getType().getRawClass();
            if (Objects.equals(clazz, Long.class) || Objects.equals(clazz, long.class)
                    || Objects.equals(clazz, Integer.class) || Objects.equals(clazz, int.class)
                    || Objects.equals(clazz, String.class)
                    || clazz.isEnum()
                ) {
                JsonDictSerialize dict = beanProperty.getAnnotation(JsonDictSerialize.class);
                if (dict != null) {
                    String name = beanProperty.getName();

                    String serializeName = dict.serializeName();
                    if(StringUtils.isBlank(serializeName)) {
                        serializeName = name+"Descp";
                    }
                    String field = dict.field();
                    if(StringUtils.isBlank(field)) {//没有指定field的时候， 默认值
                        field = name;
                    }
                    String prefix = dict.prefix();

                    return new DictSerializer(dict.value(), field, serializeName, prefix, dict.multi());
                }
            }
            return provider.findValueSerializer(beanProperty.getType(), beanProperty);
        }
        return provider.findNullValueSerializer(null);
    }
}