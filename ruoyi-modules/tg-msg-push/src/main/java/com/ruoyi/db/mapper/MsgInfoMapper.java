package com.ruoyi.db.mapper;

import com.ruoyi.common.mp.dao.IBaseDao;
import com.ruoyi.db.domain.MsgInfo;

import java.util.List;

/**
 * 用户Mapper接口
 * 
 * @author ruoyi
 * @date 2025-06-09
 */
public interface MsgInfoMapper extends IBaseDao<MsgInfo>
{

    List<MsgInfo> selectJobAll();
}
