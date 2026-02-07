package com.ruoyi.web.dict.serialize;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.annotation.JacksonStdImpl;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import com.ruoyi.common.constant.BaseEnum;
import com.ruoyi.common.core.utils.StringUtils;
import com.ruoyi.common.entity.DictMap;

import java.io.IOException;

@JacksonStdImpl
public class EnumSerializer extends StdSerializer<BaseEnum> {

    public EnumSerializer() {
        this(BaseEnum.class);
    }

    public EnumSerializer(Class<BaseEnum> handledType) {
        super(handledType, false);
    }

    @Override
    public void serialize(BaseEnum o, JsonGenerator gen, SerializerProvider provider) throws IOException {

        String fieldName = gen.getOutputContext().getCurrentName();
        if(StringUtils.isNotBlank(fieldName)) {
            gen.writeString(o.getValue());
            gen.writeStringField(fieldName + "Descp", o.getDescp());
        }else {
            gen.writeObject(new DictMap(o.getValue(), o.getDescp()));
        }
    }
}