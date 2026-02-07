package com.ruoyi.web.dict.serialize;

import com.alibaba.fastjson2.JSONObject;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.ruoyi.common.core.utils.SpringUtils;
import com.ruoyi.common.core.utils.StringUtils;
import com.ruoyi.common.constant.DictSpace;
import com.ruoyi.web.dict.service.DictService;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class DictSerializer extends JsonSerializer{

    public static final ConcurrentHashMap<String, Map<String, JSONObject>> concurrentHashMap = new ConcurrentHashMap<>();

    private DictService dictService;

    private DictSpace space;

    private String field;

    private String serializeName;

    private String prefix;

    private boolean multi;//多选项

    public DictSerializer(DictSpace space, String field, String serializeName, String prefix, boolean multi) {
        this.space = space;
        this.field = field;
        this.serializeName = serializeName;
        this.prefix = prefix;
        this.multi = multi;

        if(dictService == null) {
            dictService = SpringUtils.getBean("dictService");
        }
    }

    public static ConcurrentHashMap<String, Map<String, JSONObject>> getHolder() {
        return concurrentHashMap;
    }

    @Override
    public void serialize(Object o, JsonGenerator gen, SerializerProvider provider) throws IOException {
        provider.defaultSerializeValue(o, gen);

        //TODO 取线程缓存 这里有个问题， 如果数据量非常大，不建议使用这个
        Map<String, JSONObject> obj = concurrentHashMap.get(space.name());
        if(obj == null) {
            //取redis缓存
            obj = dictService.getDictDataMap(space);
            concurrentHashMap.put(space.name(), obj);
        }

        if(multi) {
            String[] keyArr = o.toString().split(",");

            StringBuffer sb = new StringBuffer("");
            for(String key : keyArr) {
                String realKey = key;
                if(StringUtils.isNotBlank(this.prefix)) {
                    realKey = buildDictKey(this.prefix, realKey);
                }

                JSONObject cacheObj = obj.get(realKey);
                String val = cacheObj == null ? "":cacheObj.getString(field);

                sb.append(val+",");
            }

            gen.writeStringField(serializeName, sb.substring(0, sb.lastIndexOf(",")).toString());
        }else {
            String realKey = o.toString();
            if(StringUtils.isNotBlank(this.prefix)) {
                realKey = buildDictKey(this.prefix, realKey);
            }

            JSONObject cacheObj = obj.get(realKey);
            String val = cacheObj == null ? null:cacheObj.getString(field);
            gen.writeStringField(serializeName, val);
        }
    }

    public static String buildDictKey(String prefix, String realKey) {
        return prefix +"@" + realKey;
    }

    public static void clearData(DictSpace space) {
        concurrentHashMap.remove(space.name());
    }

}