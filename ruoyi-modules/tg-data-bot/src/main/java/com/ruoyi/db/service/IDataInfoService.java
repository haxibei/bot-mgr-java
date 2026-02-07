package com.ruoyi.db.service;

import com.ruoyi.common.mp.service.IBaseService;
import com.ruoyi.db.domain.DataInfo;
import com.ruoyi.db.domain.DomainInfo;
import com.ruoyi.model.DataScore;

import java.util.List;

/**
 * 充值订单Service接口
 * 
 * @author ruoyi
 * @date 2025-06-09
 */
public interface IDataInfoService extends IBaseService<DataInfo>
{

    void spiderData(String date);

    List<DataInfo> loadRemoteData(DomainInfo domainInfo, String agentCode, String date);

    void refreshTk(String date);

    void pushDataMsg(String date);

    List<DataScore> getConvertConfig(String botId);

    List<DataScore> getRenewConfig(String botId);
}
