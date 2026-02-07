package com.ruoyi.db.service.impl;

import com.ruoyi.common.mp.service.impl.BaseServiceImpl;
import com.ruoyi.db.domain.AgentBindInfo;
import com.ruoyi.db.mapper.AgentBindInfoMapper;
import com.ruoyi.db.service.IAgentBindInfoService;
import lombok.NonNull;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 充值订单Service业务层处理
 * 
 * @author ruoyi
 * @date 2025-06-09
 */
@Service
public class AgentBindInfoServiceImpl extends BaseServiceImpl<AgentBindInfo, AgentBindInfoMapper> implements IAgentBindInfoService
{

    @Override
    public void delData(Long userId, String domain, String agentCode, String botId) {
        this.getBaseMapper().delData(userId, domain, agentCode, botId);
    }

    @Override
    public List<String> selectAllGroup(String botId) {
        return this.getBaseMapper().selectAllGroup(botId);
    }

    @Override
    public void pauseSendMsg(@NonNull Long userId) {
        this.getBaseMapper().pauseSendMsg(userId);
    }

    @Override
    public void startSendMsg(@NonNull Long userId) {
        this.getBaseMapper().startSendMsg(userId);
    }
}
