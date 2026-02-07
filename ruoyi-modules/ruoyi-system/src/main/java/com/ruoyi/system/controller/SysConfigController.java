package com.ruoyi.system.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.ruoyi.common.core.domain.R;
import com.ruoyi.common.core.utils.StringUtils;
import com.ruoyi.common.core.utils.poi.ExcelUtil;
import com.ruoyi.common.entity.PageData;
import com.ruoyi.common.log.annotation.Log;
import com.ruoyi.common.log.enums.BusinessType;
import com.ruoyi.common.mp.service.IBaseService;
import com.ruoyi.common.security.annotation.RequiresPermissions;
import com.ruoyi.system.domain.SysConfig;
import com.ruoyi.system.service.ISysConfigService;
import com.ruoyi.web.base.BaseController;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 参数配置 信息操作处理
 * 
 * @author ruoyi
 */
@RestController
@RequestMapping("/config")
public class SysConfigController extends BaseController<SysConfig>
{
    @Autowired
    private ISysConfigService configService;

    @Override
    protected R<PageData<SysConfig>> listData(SysConfig queryData) {
        queryData.setOrdseg("priority");
        return super.listData(queryData);
    }

    @Log(title = "参数管理", businessType = BusinessType.EXPORT)
    @RequiresPermissions("system:config:export")
    @PostMapping("/export")
    public void export(HttpServletResponse response, SysConfig config)
    {
        List<SysConfig> list = configService.selectList(config);
        ExcelUtil<SysConfig> util = new ExcelUtil<SysConfig>(SysConfig.class);
        util.exportExcel(response, list, "参数数据");
    }

    /**
     * 根据参数键名查询参数值
     */
    @GetMapping(value = "/configKey/{configKey}")
    public R<String> getConfigKey(@PathVariable String configKey)
    {
        return R.ok(configService.selectConfigByKey(configKey));
    }

    @Override
    public R save(SysConfig config) {
        if (!configService.checkConfigKeyUnique(config))
        {
            return R.fail("新增参数'" + config.getConfigName() + "'失败，参数键名已存在");
        }
        return R.ok(configService.doSave(config));
    }

    @Override
    public R update(SysConfig config) {
        if (!configService.checkConfigKeyUnique(config))
        {
            return R.fail("新增参数'" + config.getConfigName() + "'失败，参数键名已存在");
        }
        return R.ok(configService.doSave(config));
    }

    @Override
    public R delete(Long[] pks) {
        configService.deleteConfigByIds(pks);
        return R.ok();
    }

    /**
     * 刷新参数缓存
     */
    @RequiresPermissions("system:config:remove")
    @Log(title = "参数管理", businessType = BusinessType.CLEAN)
    @DeleteMapping("/refreshCache")
    public R refreshCache()
    {
        configService.resetConfigCache();
        return R.ok();
    }

    @Override
    public String getModule() {
        return "system:config";
    }

    @Override
    public String getModuleName() {
        return "参数管理";
    }

    @Override
    public IBaseService<SysConfig> getService() {
        return configService;
    }


    @GetMapping("/getConfigWithoutLogin")
    @ApiOperation("免登录获取部分配置")
    public R<Map<String, String>> getConfigWithoutLogin(String keys)
    {
        if(StringUtils.isBlank(keys)) {
            return R.ok(new HashMap<>());
        }

        SysConfig queryData = new SysConfig();
        QueryWrapper<SysConfig> baseWrapper = configService.getBaseWrapper(queryData);
        baseWrapper.lambda().in(SysConfig::getConfigKey, keys.split(","));

        List<SysConfig> configs = configService.list(baseWrapper);

        Map<String, String> map = new HashMap<>();
        if(CollectionUtils.isNotEmpty(configs)) {
            for (SysConfig config : configs) {
                map.put(config.getConfigKey(), config.getConfigValue());
            }
        }
        return R.ok(map);
    }
}
