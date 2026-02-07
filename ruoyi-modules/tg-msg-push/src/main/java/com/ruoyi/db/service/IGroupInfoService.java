package com.ruoyi.db.service;

import com.ruoyi.common.mp.service.IBaseService;
import com.ruoyi.db.domain.GroupInfo;

import java.util.List;

/**
 * 充值订单Service接口
 * 
 * @author ruoyi
 * @date 2025-06-09
 */
public interface IGroupInfoService extends IBaseService<GroupInfo>
{


    GroupInfo getByUserName(String userName, String tgId);

    GroupInfo getByGroupId(String groupId, String tgId);
}
