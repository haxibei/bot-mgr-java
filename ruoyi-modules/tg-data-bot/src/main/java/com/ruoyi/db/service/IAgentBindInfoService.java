package com.ruoyi.db.service;

import com.ruoyi.common.mp.service.IBaseService;
import com.ruoyi.db.domain.AgentBindInfo;
import lombok.NonNull;

import java.util.List;

/**
 * 充值订单Service接口
 * 
 * @author ruoyi
 * @date 2025-06-09
 */
public interface IAgentBindInfoService extends IBaseService<AgentBindInfo>
{

    void delData(Long userId, String domain, String agentCode, String botId);

    List<String> selectAllGroup(String botId);

    void pauseSendMsg(@NonNull Long userId);

    void startSendMsg(@NonNull Long id);
}
