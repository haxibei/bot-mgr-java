package com.ruoyi.common.constant;

import java.util.List;

public enum DictSpace {
    Null("", "", "", "空"),
    // 系统模块
    Dept("sys_dept", "dept_id", "select dept_id,dept_name,concat(dept_id,',', ancestors) ancestors from sys_dept", "部门字典"),

    Position("sys_post", "post_id", "select post_id,post_name,post_code,del_flag from sys_post", "岗位字典"),
    RoleDept("sys_role_dept", "role_id", "select role_id,group_concat(dept_id) ancestors from sys_role_dept group by role_id", "部门角色对应关系"),
    SysUserInfo("sys_user", "user_id", "select user_id,user_name,nick_name,dept_id from sys_user", "用户信息"),

    SysDict("sys_dict_data", "dict_value", "select concat(dict_type,'@', dict_value) as dict_value,dict_label from sys_dict_data", "系统字典信息"),

    SysRole("sys_role", "role_key", "select role_key,role_name from sys_role", "系统角色"),

    //adm
    AdmProject("project_info", "project_id", "select project_id,project_name,project_code from project_info", "商务项目"),
    ;

    private String table;

    private String primaryKey;

    private String selectSql;

    private String descp;

    DictSpace(String table, String primaryKey, String selectSql, String descp) {
        this.table = table;
        this.primaryKey = primaryKey;
        this.selectSql = selectSql;
        this.descp = descp;
    }

    public String getTable() {
        return table;
    }

    public String getPrimaryKey() {
        return primaryKey;
    }

    public String getSelectSql() {
        return selectSql;
    }



}
