package com.ruoyi.web.dict.service;

import com.ruoyi.common.core.utils.SpringUtils;
import com.ruoyi.common.constant.DictSpace;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Slf4j
public abstract class InitDictService {


    protected List<DictSpace> getSpaces() {
        return new ArrayList<>();
    }

    @PostConstruct
    public void init() {

            CompletableFuture.runAsync(new Runnable() {
                @Override
                public void run() {
                    try {
                        log.info("异步执行加载字典到缓存");

                        List<DictSpace> spaces = getSpaces();
                        if(CollectionUtils.isNotEmpty(spaces)) {
                            DictService dictService = SpringUtils.getBean("dictService");
                            spaces.stream().forEach(space -> {
                                dictService.reloadData(space);
                            });
                        }
                        log.info("异步执行加载字典到缓存完成");
                    }catch (Exception e) {
                        log.error("业务字典 缓存加载失败", e);
                    }
                }
            });

    }
}
