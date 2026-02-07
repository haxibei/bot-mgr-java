package com.ruoyi.system.utils;

import com.alibaba.fastjson2.JSONArray;
import com.ruoyi.common.core.constant.CacheConstants;
import com.ruoyi.common.core.utils.StringUtils;
import com.ruoyi.common.redis.constant.DefaultJedisKeyNS;
import com.ruoyi.common.redis.service.RedisService;
import com.ruoyi.system.domain.SysDictData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;

/**
 * 字典工具类
 * 
 * @author ruoyi
 */
@Component
public class DictUtils
{
    @Autowired
    private RedisService redisService;
    
    /**
     * 设置字典缓存
     * 
     * @param key 参数键
     * @param dictDatas 字典数据列表
     */
    public void setDictCache(String key, List<SysDictData> dictDatas)
    {
        redisService.setCacheObject(DefaultJedisKeyNS.dict, getCacheKey(key), dictDatas);
    }

    /**
     * 获取字典缓存
     * 
     * @param key 参数键
     * @return dictDatas 字典数据列表
     */
    public List<SysDictData> getDictCache(String key)
    {
        JSONArray arrayCache = redisService.getCacheObject(DefaultJedisKeyNS.dict, getCacheKey(key));
        if (StringUtils.isNotNull(arrayCache))
        {
            return arrayCache.toList(SysDictData.class);
        }
        return null;
    }

    /**
     * 删除指定字典缓存
     * 
     * @param key 字典键
     */
    public void removeDictCache(String key)
    {
        redisService.deleteObject(DefaultJedisKeyNS.dict, getCacheKey(key));
    }

    /**
     * 清空字典缓存
     */
    public void clearDictCache()
    {
        Collection<String> keys = redisService.keys(CacheConstants.SYS_DICT_KEY + "*");
        redisService.deleteObject(keys);
    }

    /**
     * 设置cache key
     * 
     * @param configKey 参数键
     * @return 缓存键key
     */
    public String getCacheKey(String configKey)
    {
        return CacheConstants.SYS_DICT_KEY + configKey;
    }
}
