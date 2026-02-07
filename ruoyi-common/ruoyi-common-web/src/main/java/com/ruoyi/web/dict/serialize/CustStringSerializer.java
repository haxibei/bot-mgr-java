package com.ruoyi.web.dict.serialize;

import com.alibaba.fastjson2.JSONObject;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JacksonStdImpl;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.ruoyi.web.dict.service.DictService;
import com.ruoyi.common.constant.BaseEnum;
import com.ruoyi.common.constant.DictSpace;
import com.ruoyi.common.core.utils.SpringUtils;
import com.ruoyi.common.core.utils.StringUtils;
import com.ruoyi.common.entity.DictMap;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@JacksonStdImpl
public class CustStringSerializer extends ToStringSerializer {

    private DictService dictService;

    public CustStringSerializer() {
        if(dictService == null) {
            dictService = SpringUtils.getBean("dictService");
        }
    }

    @Override
    public void serialize(Object value, JsonGenerator gen, SerializerProvider provider)
            throws IOException
    {
        if("createBy".equals(gen.getOutputContext().getCurrentName())) {
            String val = valueToString(value);
            gen.writeString(val);

            //TODO 取线程缓存 这里有个问题， 如果数据量非常大，不建议使用这个
            ConcurrentHashMap<String, Map<String, JSONObject>> concurrentHashMap = DictSerializer.getHolder();

            DictSpace space = DictSpace.SysUserInfo;
            Map<String, JSONObject> obj = concurrentHashMap.get(space.name());
            if(obj == null) {
                //取redis缓存
                obj = dictService.getDictDataMap(space);
                concurrentHashMap.put(space.name(), obj);
            }

            JSONObject cacheObj = obj.get(val);
            gen.writeStringField("createUserName", cacheObj == null?"":cacheObj.getString("nick_name"));
        }else {
            gen.writeString(valueToString(value));
        }
    }

}
