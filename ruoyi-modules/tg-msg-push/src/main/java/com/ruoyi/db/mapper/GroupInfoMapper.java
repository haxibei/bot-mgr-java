package com.ruoyi.db.mapper;

import com.ruoyi.common.mp.dao.IBaseDao;
import com.ruoyi.db.domain.GroupInfo;
import org.apache.ibatis.annotations.Select;

/**
 * 充值订单Mapper接口
 * 
 * @author ruoyi
 * @date 2025-06-09
 */
public interface GroupInfoMapper extends IBaseDao<GroupInfo>
{

    @Select("select * from group_info where tg_id = #{tgId} and link like concat('%', #{groupId})")
    GroupInfo getByGroupId(String groupId, String tgId);
}
