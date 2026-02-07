package com.ruoyi.system.mapper;

import java.util.List;

import com.ruoyi.common.mp.dao.IBaseDao;
import com.ruoyi.system.domain.SysPost;
import org.apache.ibatis.annotations.Param;

/**
 * 岗位信息 数据层
 * 
 * @author ruoyi
 */
public interface SysPostMapper extends IBaseDao<SysPost>
{
    /**
     * 根据用户ID获取岗位选择框列表
     *
     * @param userId 用户ID
     * @return 选中岗位ID列表
     */
    List<Long> selectPostListByUserId(Long userId);

    /**
     * 查询用户所属岗位组
     *
     * @param userName 用户名
     * @return 结果
     */
    List<SysPost> selectPostsByUserName(String userName);

    /**
     * 校验岗位名称
     *
     * @param postName 岗位名称
     * @return 结果
     */
    SysPost checkPostNameUnique(String postName);

    /**
     * 校验岗位编码
     *
     * @param postCode 岗位编码
     * @return 结果
     */
    SysPost checkPostCodeUnique(String postCode);

    List<Long> getRoleIds(Long deptId, Long[] postIds);
}