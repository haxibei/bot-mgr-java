package com.ruoyi.db.mapper;

import com.ruoyi.common.mp.dao.IBaseDao;
import com.ruoyi.db.domain.AgentBindInfo;
import com.ruoyi.db.domain.DataInfo;
import com.ruoyi.model.DataScore;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 充值订单Mapper接口
 * 
 * @author ruoyi
 * @date 2025-06-09
 */
public interface DataInfoMapper extends IBaseDao<DataInfo>
{

    List<DataInfo> loadPushData(@Param("date") String date);

    List<DataScore> getConvertConfig(@Param("botId") String botId);

    List<DataScore> getRenewConfig(@Param("botId") String botId);

    List<AgentBindInfo> getAllAgentCode(@Param("domain") String domain, @Param("level") int level);
}
