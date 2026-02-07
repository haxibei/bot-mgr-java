package com.ruoyi.web.dict.service;

import com.alibaba.fastjson2.JSONObject;
import com.ruoyi.common.core.constant.CacheConstants;
import com.ruoyi.common.mp.dao.CustomerSqlMapper;
import com.ruoyi.common.redis.constant.DefaultJedisKeyNS;
import com.ruoyi.common.redis.service.RedisService;
import com.ruoyi.common.constant.DictSpace;
import com.ruoyi.web.dict.serialize.DictSerializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class DictService {

    @Autowired
    private RedisService redisService;

    @Autowired
    private CustomerSqlMapper customerSqlMapper;

    public String getDictVal(DictSpace space, String key, String field) {

        JSONObject dictData = getDictData(space, key);
        return dictData == null ? "":dictData.getString(field);
    }

    public JSONObject getDictData(DictSpace space, String key) {
        JSONObject cacheMap = (JSONObject) redisService.getCacheMapData(DefaultJedisKeyNS.dict, buildKey(space.name()), key);
        return cacheMap;
    }

    public void setDictData(DictSpace space, List<Map<String, Object>> datas) {
        Map<String, Map<String, Object>> dictData = new HashMap<>();
        for(Map<String, Object> data : datas) {
            dictData.put(data.get(space.getPrimaryKey()).toString(), data);
        }
        redisService.setCacheMap(DefaultJedisKeyNS.dict, buildKey(space.name()), dictData);
    }

    public void updateDictVal(DictSpace space, Map<String, Object> dictData) {
        redisService.setCacheMapValue(DefaultJedisKeyNS.dict, space.name(), dictData.get(space.getPrimaryKey()).toString(), dictData);
    }

    public void removeDictVal(DictSpace space, String primaryKey) {
        redisService.deleteCacheMapValue(DefaultJedisKeyNS.dict, space.name(), primaryKey);
    }

    private String buildKey(String name) {
        return CacheConstants.BUSI_DICT_KEY+name;
    }

    public void reloadData(DictSpace space) {
        if(!DictSpace.Null.equals(space)) {
            String sql = space.getSelectSql();

            List<Map<String, Object>> maps = customerSqlMapper.executeQuerySql(sql);
            setDictData(space, maps);
        }
    }

    public Map<String, JSONObject> getDictDataMap(DictSpace space) {
        return redisService.getCacheMap(DefaultJedisKeyNS.dict, buildKey(space.name()));
    }

    public Map<String, JSONObject> getDictDataMap(DictSpace space, String prefix) {
        Map<String, JSONObject> dictDataMap = getDictDataMap(space);

        Map<String, JSONObject> dictData = new HashMap<>();
        for(String key : dictDataMap.keySet()) {
            if(key.startsWith(prefix+"@")) {
                dictData.put(key, dictDataMap.get(key));
            }
        }
        return dictData;
    }

    public String getDictDataMapVal(DictSpace space, String key, String dictLabel) {

        JSONObject cacheMap = (JSONObject) redisService.getCacheMapData(DefaultJedisKeyNS.dict, buildKey(space.name()), key);
        if(cacheMap == null) {
            return "";
        }
        return cacheMap.get(dictLabel) == null?"":cacheMap.get(dictLabel).toString();

    }
}
