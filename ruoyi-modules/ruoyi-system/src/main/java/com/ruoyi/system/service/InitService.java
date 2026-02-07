package com.ruoyi.system.service;

import com.ruoyi.common.constant.DictSpace;
import com.ruoyi.web.dict.service.InitDictService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * 初始化
 * 
 * @author xingruan
 */
@Service
@Slf4j
public class InitService extends InitDictService
{


    @Autowired
    private ISysConfigService configService;

    @Autowired
    private ISysDictTypeService dictTypeService;

    /**
     * 项目启动时，初始化参数到缓存
     */
    @Override
    public void init() {
        super.init();
        CompletableFuture.runAsync(new Runnable() {
            @Override
            public void run() {
                try {
                    dictTypeService.loadingDictCache();
                }catch (Exception e) {
                    log.error("dictType 缓存加载失败", e);
                }
            }
        });
        CompletableFuture.runAsync(new Runnable() {
            @Override
            public void run() {
                try {
                    configService.loadingConfigCache();
                }catch (Exception e) {
                    log.error("config 缓存加载失败", e);
                }
            }
        });
    }


    public static List<DictSpace> spaces = Arrays.asList(
            DictSpace.Dept,
            DictSpace.RoleDept,
            DictSpace.SysRole,
            DictSpace.SysUserInfo,
            DictSpace.SysDict,
            DictSpace.Position
    );
    @Override
    public List<DictSpace> getSpaces() {
        return spaces;
    }
}
