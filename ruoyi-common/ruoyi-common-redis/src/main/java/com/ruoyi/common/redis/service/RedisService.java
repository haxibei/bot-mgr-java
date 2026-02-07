package com.ruoyi.common.redis.service;

import com.ruoyi.common.core.domain.DbModifyMsg;
import com.ruoyi.common.redis.constant.RedisKeyNS;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.*;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * spring redis 工具类
 *
 * @author ruoyi
 **/
@SuppressWarnings(value = { "unchecked", "rawtypes" })
@Component
@Slf4j
public class RedisService
{
    @Autowired
    public RedisTemplate redisTemplate;

    public RedisTemplate getRedisTemplate() {
        return redisTemplate;
    }

    public BigDecimal getUniqueAmount(RedisKeyNS nameSpace, BigDecimal amount, Long seconds) {
        BigDecimal decimal = new BigDecimal(RandomUtils.nextInt(0, 19) - 9).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
        BigDecimal val = amount.add(decimal);

        String key = val.toString();
        //TODO 极端情况这里的数字全部被占用， 则会进入死循环
        int maxCycle = 20;
        while(!putIfAbsent(nameSpace, key, 1, seconds)) {
            if(maxCycle < 1) {
                throw new RuntimeException("生成订单金额失败，请稍后再试");
            }
            decimal = new BigDecimal(RandomUtils.nextInt(0, 19) - 9).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
            val = amount.add(decimal);
            key = val.toString();

            maxCycle --;
        }
        return decimal;
    }

    public void removeUniqueAmount(RedisKeyNS nameSpace, BigDecimal usdtOrderAmount) {
        String key = usdtOrderAmount.toString();
        redisTemplate.delete(buildKey(nameSpace, key));
    }

    public boolean putIfAbsent(RedisKeyNS nameSpace, String id, final Object value)
    {
        String key = buildKey(nameSpace, id);
        return redisTemplate.opsForValue().setIfAbsent(key, value, nameSpace.getExpire(), TimeUnit.SECONDS);
    }

    public boolean putIfAbsent(RedisKeyNS nameSpace, String id, final Object value, long seconds)
    {
        String key = buildKey(nameSpace, id);
        return redisTemplate.opsForValue().setIfAbsent(key, value, seconds, TimeUnit.SECONDS);
    }

    /**
     * 缓存基本的对象，Integer、String、实体类等
     *
     * @param nameSpace:id Redis键
     * @param value 缓存的值
     */
    public <T> void setCacheObject(RedisKeyNS nameSpace, String id, final T value)
    {
        String key = buildKey(nameSpace, id);
        if(nameSpace.getExpire() > 0) {
            redisTemplate.opsForValue().set(key, value, nameSpace.getExpire(), TimeUnit.SECONDS);
        }else {
            redisTemplate.opsForValue().set(key, value);
        }
    }

    /**
     * 缓存基本的对象，Integer、String、实体类等
     *
     * @param nameSpace:id Redis键
     * @param value 缓存的值
     * @param timeout 时间
     * @param timeUnit 时间颗粒度
     */
    public <T> void setCacheObject(RedisKeyNS nameSpace, String id, final T value, final Long timeout, final TimeUnit timeUnit)
    {
        String key = buildKey(nameSpace, id);
        redisTemplate.opsForValue().set(key, value, timeout, timeUnit);
    }

    /**
     * 设置有效时间
     *
     * @param nameSpace:id Redis键
     * @param timeout 超时时间
     * @return true=设置成功；false=设置失败
     */
    public boolean expire(RedisKeyNS nameSpace, String id, final long timeout)
    {
        return expire(nameSpace, id, timeout, TimeUnit.SECONDS);
    }

    /**
     * 设置有效时间
     *
     * @param nameSpace:id Redis键
     * @param timeout 超时时间
     * @param unit 时间单位
     * @return true=设置成功；false=设置失败
     */
    public boolean expire(RedisKeyNS nameSpace, String id, final long timeout, final TimeUnit unit)
    {
        return redisTemplate.expire(buildKey(nameSpace, id), timeout, unit);
    }

    /**
     * 获取有效时间
     *
     * @param nameSpace:id Redis键
     * @return 有效时间
     */
    public long getExpire(RedisKeyNS nameSpace, String id)
    {
        return redisTemplate.getExpire(buildKey(nameSpace, id));
    }

    /**
     * 判断 key是否存在
     *
     * @param nameSpace:id Redis键
     * @return true 存在 false不存在
     */
    public Boolean hasKey(RedisKeyNS nameSpace, String id)
    {
        return redisTemplate.hasKey(buildKey(nameSpace, id));
    }

    /**
     * 获得缓存的基本对象。
     *
     * @param nameSpace:id Redis键
     * @return 缓存键值对应的数据
     */
    public <T> T getCacheObject(RedisKeyNS nameSpace, String id)
    {
        ValueOperations<String, T> operation = redisTemplate.opsForValue();
        return operation.get(buildKey(nameSpace, id));
    }

    /**
     * 删除单个对象
     *
     * @param nameSpace:id Redis键
     */
    public boolean deleteObject(RedisKeyNS nameSpace, String id)
    {
        return redisTemplate.delete(buildKey(nameSpace, id));
    }

    /**
     * 删除集合对象
     *
     * @param collection 多个对象
     * @return
     */
    public boolean deleteObject(final Collection collection)
    {
        return redisTemplate.delete(collection) > 0;
    }

    /**
     * 缓存List数据
     *
     * @param nameSpace:id Redis键
     * @param dataList 待缓存的List数据
     * @return 缓存的对象
     */
    public <T> long setCacheList(RedisKeyNS nameSpace, String id, final List<T> dataList)
    {
        String key = buildKey(nameSpace, id);
        Long count = redisTemplate.opsForList().rightPushAll(key, dataList);
        redisTemplate.expire(key, nameSpace.getExpire(), TimeUnit.SECONDS);
        return count == null ? 0 : count;
    }

    public <T> void rightPush(RedisKeyNS nameSpace, String id, final T data)
    {
        String key = buildKey(nameSpace, id);
        Long count = redisTemplate.opsForList().rightPush(key, data);
        if(nameSpace.getExpire() > 0) {
            redisTemplate.expire(key, nameSpace.getExpire(), TimeUnit.SECONDS);
        }
    }
    public <T> void leftPush(RedisKeyNS nameSpace, String id, final T data)
    {
        String key = buildKey(nameSpace, id);
        Long count = redisTemplate.opsForList().leftPush(key, data);
        if(nameSpace.getExpire() > 0) {
            redisTemplate.expire(key, nameSpace.getExpire(), TimeUnit.SECONDS);
        }
    }
    public <T> T leftPop(RedisKeyNS nameSpace, String id) {
        String key = buildKey(nameSpace, id);
        ListOperations<String, T> opt = redisTemplate.opsForList();
        return opt.leftPop(key);
    }

    /**
     * 获得缓存的list对象
     *
     * @param nameSpace:id Redis键
     * @return 缓存键值对应的数据
     */
    public <T> List<T> getCacheList(RedisKeyNS nameSpace, String id)
    {
        return redisTemplate.opsForList().range(buildKey(nameSpace, id), 0, -1);
    }

    /**
     * 缓存Set
     *
     * @param nameSpace:id Redis键
     * @param dataSet 缓存的数据
     * @return 缓存数据的对象
     */
    public <T> BoundSetOperations<String, T> setCacheSet(RedisKeyNS nameSpace, String id, final Set<T> dataSet)
    {
        String key = buildKey(nameSpace, id);
        BoundSetOperations<String, T> setOperation = redisTemplate.boundSetOps(key);
        Iterator<T> it = dataSet.iterator();
        while (it.hasNext())
        {
            setOperation.add(it.next());
        }
        redisTemplate.expire(key, nameSpace.getExpire(), TimeUnit.SECONDS);
        return setOperation;
    }

    /**
     * 获得缓存的set
     *
     * @param nameSpace:id Redis键
     * @return
     */
    public <T> Set<T> getCacheSet(RedisKeyNS nameSpace, String id)
    {
        return redisTemplate.opsForSet().members(buildKey(nameSpace, id));
    }

    /**
     * 缓存Map
     *
     * @param nameSpace:id Redis键
     * @param dataMap
     */
    public <T> void setCacheMap(RedisKeyNS nameSpace, String id, final Map<String, T> dataMap)
    {
        if (dataMap != null) {
            String key = buildKey(nameSpace, id);
            redisTemplate.opsForHash().putAll(key, dataMap);

            if(nameSpace.getExpire() > 0) {
                redisTemplate.expire(key, nameSpace.getExpire(), TimeUnit.SECONDS);
            }
        }
    }

    /**
     * 获得缓存的Map
     *
     * @param nameSpace:id Redis键
     * @return
     */
    public <T> Map<String, T> getCacheMap(RedisKeyNS nameSpace, String id)
    {
        return redisTemplate.opsForHash().entries(buildKey(nameSpace, id));
    }

    public Object getCacheMapData(RedisKeyNS nameSpace, String id, String hashKey)
    {
        return redisTemplate.opsForHash().get(buildKey(nameSpace, id), hashKey);
    }

    /**
     * 往Hash中存入数据
     *
     * @param nameSpace:id Redis键
     * @param hKey Hash键
     * @param value 值
     */
    public <T> void setCacheMapValue(RedisKeyNS nameSpace, String id, final String hKey, final T value)
    {
        String key = buildKey(nameSpace, id);
        redisTemplate.opsForHash().put(key, hKey, value);
        redisTemplate.expire(key, nameSpace.getExpire(), TimeUnit.SECONDS);
    }

    /**
     * 获取Hash中的数据
     *
     * @param nameSpace:id Redis键
     * @param hKey Hash键
     * @return Hash中的对象
     */
    public <T> T getCacheMapValue(RedisKeyNS nameSpace, String id, final String hKey)
    {
        HashOperations<String, String, T> opsForHash = redisTemplate.opsForHash();
        return opsForHash.get(buildKey(nameSpace, id), hKey);
    }

    /**
     * 获取多个Hash中的数据
     *
     * @param nameSpace:id Redis键
     * @param hKeys Hash键集合
     * @return Hash对象集合
     */
    public <T> List<T> getMultiCacheMapValue(RedisKeyNS nameSpace, String id, final Collection<Object> hKeys)
    {
        return redisTemplate.opsForHash().multiGet(buildKey(nameSpace, id), hKeys);
    }

    /**
     * 删除Hash中的某条数据
     *
     * @param nameSpace:id Redis键
     * @param hKey Hash键
     * @return 是否成功
     */
    public boolean deleteCacheMapValue(RedisKeyNS nameSpace, String id, final String hKey)
    {
        return redisTemplate.opsForHash().delete(buildKey(nameSpace,id), hKey) > 0;
    }

    /**
     * 获得缓存的基本对象列表
     *
     * @param pattern 字符串前缀
     * @return 对象列表
     */
    public Collection<String> keys(final String pattern)
    {
        return redisTemplate.keys(pattern);
    }

    private String buildKey(RedisKeyNS nameSpace, Serializable id) {
        if(nameSpace.getNameSpace().endsWith(RedisKeyNS.KEY_SPLITER)) {
            return nameSpace.getNameSpace() + id.toString();
        }
        return nameSpace.getNameSpace() + RedisKeyNS.KEY_SPLITER + id.toString();
    }

    public void convertAndSend(String txChannel, Object object) {
        redisTemplate.convertAndSend(txChannel, object);
    }

    /**
     * 推送消息
     *
     * @param publisher
     * @param content
     */
    public void publishDbMsg(MessagePublisher publisher, DbModifyMsg content) {
        log.info("{}发布Redis消息=====>{}", publisher, content);
        ChannelTopic topic = publisher.getChannelTopic();
        redisTemplate.convertAndSend(topic.getTopic(), content);
    }


    public String ping() {
        return redisTemplate.getConnectionFactory().getConnection().ping();
    }


}
