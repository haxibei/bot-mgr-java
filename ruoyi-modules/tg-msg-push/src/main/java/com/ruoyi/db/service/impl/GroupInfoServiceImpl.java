package com.ruoyi.db.service.impl;

import com.ruoyi.common.mp.service.impl.BaseServiceImpl;
import com.ruoyi.db.domain.GroupInfo;
import com.ruoyi.db.mapper.GroupInfoMapper;
import com.ruoyi.db.service.IGroupInfoService;
import org.springframework.stereotype.Service;

/**
 * 充值订单Service业务层处理
 * 
 * @author ruoyi
 * @date 2025-06-09
 */
@Service
public class GroupInfoServiceImpl extends BaseServiceImpl<GroupInfo, GroupInfoMapper> implements IGroupInfoService
{


    @Override
    public GroupInfo getByUserName(String userName, String tgId) {
        GroupInfo query = new GroupInfo();
        query.setUserName(userName);
        query.setTgId(tgId);
        return this.getOne(this.getBaseWrapper(query));
    }

    @Override
    public GroupInfo getByGroupId(String groupId, String tgId) {
        return this.getBaseMapper().getByGroupId(groupId, tgId);
    }
}
