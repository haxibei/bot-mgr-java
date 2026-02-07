package com.ruoyi.common.datascope.utils;

import com.ruoyi.common.constant.DictSpace;
import com.ruoyi.common.core.context.SecurityContextHolder;
import com.ruoyi.common.core.utils.StringUtils;
import com.ruoyi.common.domain.SysRole;
import com.ruoyi.common.domain.SysUser;
import com.ruoyi.web.dict.service.DictService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;

@Service
public class ScopeService {

    /**
     * 全部数据权限
     */
    public static final String DATA_SCOPE_ALL = "1";

    /**
     * 自定数据权限
     */
    public static final String DATA_SCOPE_CUSTOM = "2";

    /**
     * 部门数据权限
     */
    public static final String DATA_SCOPE_DEPT = "3";

    /**
     * 部门及以下数据权限
     */
    public static final String DATA_SCOPE_DEPT_AND_CHILD = "4";

    /**
     * 仅本人数据权限
     */
    public static final String DATA_SCOPE_SELF = "5";

    /**
     * 游客数据权限
     */
    public static final String DATA_SCOPE_GUEST = "6";

    /**
     * 数据权限过滤关键字
     */
    public static final String DATA_SCOPE = "dataScope";

    @Autowired
    private DictService dictService;

    /**
     * 获取数据权限的sql片段
     * @param user
     * @return
     */
    public StringBuilder getScopeSqlSeg(SysUser user, String deptAlias, String userAlias) {

        //获取当前接口对应所属角色
        String permission = SecurityContextHolder.getPermission();

        StringBuilder sqlString = null;
        if(StringUtils.isBlank(permission)) {//没有指定权限 则按所有角色的最大权限来
            sqlString = buildMaxPermission(user, deptAlias, userAlias);
        }else {
            List<SysRole> currentPermissionRoles = new ArrayList<>();//当前权限code 对应的角色组
            for (SysRole role : user.getRoles()) {
                Set<String> permissions = role.getPermissions();
                if (permissions.contains(permission)) {//TODO 默认 permission 只会有一个 多个后续待处理
                    currentPermissionRoles.add(role);
                }
            }
            if(!CollectionUtils.isEmpty(currentPermissionRoles)) {
                sqlString = buildMaxPermission(user, currentPermissionRoles, deptAlias, userAlias);
            }else {//没有指定权限 则按所有角色的最大权限来
                sqlString = buildMaxPermission(user, deptAlias, userAlias);
            }
        }



        return sqlString;
    }

    private StringBuilder buildMaxPermission(SysUser user, String deptAlias, String userAlias) {
        return buildMaxPermission(user, user.getRoles(), deptAlias, userAlias);
    }

    private StringBuilder buildMaxPermission(SysUser user, List<SysRole> roles, String deptAlias, String userAlias) {
        Set<String> scopes = new HashSet<>();

        Set<Long> customRoleIds = new HashSet<>();
        for (SysRole role : roles) {
            String dataScope = role.getDataScope();
            scopes.add(dataScope);

            if (DATA_SCOPE_CUSTOM.equals(dataScope)) {
                customRoleIds.add(role.getRoleId());
            }

        }
        StringBuilder sqlString = new StringBuilder();

        if(!CollectionUtils.isEmpty(scopes)) {
            Iterator<String> iterator = scopes.iterator();

            while(iterator.hasNext()) {
                String dataScope = iterator.next();

                if (DATA_SCOPE_ALL.equals(dataScope)) {
                    break;
                } else if (DATA_SCOPE_CUSTOM.equals(dataScope)) {
                    Set<String> deptIds = new HashSet<>();
                    for (Long roleId : customRoleIds) {
                        String ancestors = dictService.getDictVal(DictSpace.RoleDept, roleId + "", "ancestors");
                        deptIds.add(ancestors);
                    }
                    sqlString.append(StringUtils.format(
                            " OR {}dept_id IN ( {} ) ", StringUtils.isBlank(deptAlias)?"":(deptAlias+"."),
                            StringUtils.join(deptIds, ",")));
                } else if (DATA_SCOPE_DEPT.equals(dataScope)) {
                    sqlString.append(StringUtils.format(" OR {}dept_id = {} ", StringUtils.isBlank(deptAlias)?"":(deptAlias+"."), user.getDeptId()));
                } else if (DATA_SCOPE_DEPT_AND_CHILD.equals(dataScope)) {
                    String ancestors = dictService.getDictVal(DictSpace.Dept, user.getDeptId() + "", "ancestors");

                    sqlString.append(StringUtils.format(
                            " OR {}dept_id IN ( {} )",
                            StringUtils.isBlank(deptAlias)?"":(deptAlias+"."), ancestors));
                } else if (DATA_SCOPE_SELF.equals(dataScope)) {
                    sqlString.append(StringUtils.format(" OR {}create_by = {} ", StringUtils.isBlank(userAlias)?"":(userAlias+"."), user.getUserId()));
                } else if (DATA_SCOPE_GUEST.equals(dataScope)) {
                    sqlString.append(StringUtils.format(" OR {}guest_show = {} ", StringUtils.isBlank(userAlias)?"":(userAlias+"."), 1));
                }
            }
        }else {//没有任何角色权限， 不查询任何数据
            sqlString.append(" OR 1 = 0");
        }
        return sqlString;
    }
}
