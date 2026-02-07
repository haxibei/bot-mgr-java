package com.ruoyi.db.mapper;

import com.ruoyi.common.mp.dao.IBaseDao;
import com.ruoyi.db.domain.AgentBindInfo;
import lombok.NonNull;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 充值订单Mapper接口
 * 
 * @author ruoyi
 * @date 2025-06-09
 */
public interface AgentBindInfoMapper extends IBaseDao<AgentBindInfo>
{

    void delData(@Param("userId") Long userId, @Param("domain") String domain, @Param("agentCode") String agentCode, @Param("botId") String botId);

    List<String> selectAllGroup(@Param("botId") String botId);

    void pauseSendMsg(@NonNull @Param("userId") Long userId);

    void startSendMsg(@NonNull @Param("userId") Long userId);
}
