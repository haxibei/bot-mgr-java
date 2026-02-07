package com.ruoyi.system.service.impl;

import java.util.Collection;
import java.util.List;
import javax.annotation.PostConstruct;

import cn.hutool.core.util.RandomUtil;
import com.ruoyi.common.constant.DictSpace;
import com.ruoyi.common.mp.service.impl.BaseServiceImpl;
import com.ruoyi.common.redis.constant.DefaultJedisKeyNS;
import com.ruoyi.web.dict.service.DictService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.common.core.constant.CacheConstants;
import com.ruoyi.common.core.constant.UserConstants;
import com.ruoyi.common.core.exception.ServiceException;
import com.ruoyi.common.core.text.Convert;
import com.ruoyi.common.core.utils.StringUtils;
import com.ruoyi.common.redis.service.RedisService;
import com.ruoyi.system.domain.SysConfig;
import com.ruoyi.system.mapper.SysConfigMapper;
import com.ruoyi.system.service.ISysConfigService;

/**
 * 参数配置 服务层实现
 * 
 * @author ruoyi
 */
@Service
public class SysConfigServiceImpl extends BaseServiceImpl<SysConfig, SysConfigMapper> implements ISysConfigService
{
    @Autowired
    private RedisService redisService;

    @Autowired
    private DictService dictService;

    /**
     * 根据键名查询参数配置信息
     *
     * @param configKey 参数key
     * @return 参数键值
     */
    @Override
    public String selectConfigByKey(String configKey)
    {
        String configValue = Convert.toStr(redisService.getCacheObject(DefaultJedisKeyNS.global_config, getCacheKey(configKey)));
        if (StringUtils.isNotEmpty(configValue))
        {
            return configValue;
        }
        SysConfig config = new SysConfig();
        config.setConfigKey(configKey);
        SysConfig retConfig = this.getOne(this.getBaseWrapper(config));
        if (StringUtils.isNotNull(retConfig))
        {
            redisService.setCacheObject(DefaultJedisKeyNS.global_config, getCacheKey(configKey), retConfig.getConfigValue());
            return retConfig.getConfigValue();
        }
        return StringUtils.EMPTY;
    }

    @Override
    public SysConfig doSave(SysConfig sysConfig) {
        SysConfig dbData = super.doSave(sysConfig);

        redisService.setCacheObject(DefaultJedisKeyNS.global_config, getCacheKey(dbData.getConfigKey()), dbData.getConfigValue());
        return dbData;
    }


    /**
     * 批量删除参数信息
     *
     * @param configIds 需要删除的参数ID
     */
    @Override
    public void deleteConfigByIds(Long[] configIds)
    {
        for (Long configId : configIds)
        {
            SysConfig config = this.getById(configId);
            if (StringUtils.equals(UserConstants.YES, config.getConfigType()))
            {
                throw new ServiceException(String.format("内置参数【%1$s】不能删除 ", config.getConfigKey()));
            }
            this.removeById(configId);
            redisService.deleteObject(DefaultJedisKeyNS.global_config, getCacheKey(config.getConfigKey()));
        }
    }

    /**
     * 加载参数缓存数据
     */
    @Override
    public void loadingConfigCache()
    {
        List<SysConfig> configsList = this.selectList(new SysConfig());
        for (SysConfig config : configsList)
        {
            redisService.setCacheObject(DefaultJedisKeyNS.global_config, getCacheKey(config.getConfigKey()), config.getConfigValue());
        }
        dictService.reloadData(DictSpace.SysDict);
    }

    /**
     * 清空参数缓存数据
     */
    @Override
    public void clearConfigCache()
    {
        Collection<String> keys = redisService.keys(DefaultJedisKeyNS.global_config + "*");
        redisService.deleteObject(keys);
    }

    /**
     * 重置参数缓存数据
     */
    @Override
    public void resetConfigCache()
    {
        clearConfigCache();
        loadingConfigCache();
    }

    /**
     * 校验参数键名是否唯一
     *
     * @param config 参数配置信息
     * @return 结果
     */
    @Override
    public boolean checkConfigKeyUnique(SysConfig config)
    {
        Long configId = StringUtils.isNull(config.getConfigId()) ? -1L : config.getConfigId();

        SysConfig info = new SysConfig();
        info.setConfigKey(config.getConfigKey());
        SysConfig retConfig = this.getOne(this.getBaseWrapper(info));

        if (StringUtils.isNotNull(retConfig) && retConfig.getConfigId().longValue() != configId.longValue())
        {
            return UserConstants.NOT_UNIQUE;
        }
        return UserConstants.UNIQUE;
    }

    /**
     * 设置cache key
     *
     * @param configKey 参数键
     * @return 缓存键key
     */
    private String getCacheKey(String configKey)
    {
//        return CacheConstants.SYS_CONFIG_KEY + configKey;

        return configKey;
    }



    /**
     * 项目启动时，初始化参数到缓存
     */
    @PostConstruct
    public void init()
    {
        loadingConfigCache();
    }


    public static void main(String[] args) {
        System.out.println(RandomUtil.randomString(8));
    }

}
