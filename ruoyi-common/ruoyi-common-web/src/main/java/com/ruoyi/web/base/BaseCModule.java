package com.ruoyi.web.base;

import com.ruoyi.common.entity.BaseEntity;
import com.ruoyi.common.entity.CModule;
import com.ruoyi.common.entity.CommonEntity;
import com.ruoyi.common.mp.service.IBaseService;

/**
 */
public interface BaseCModule<T extends CommonEntity> extends CModule {

    IBaseService<T> getService();
}
