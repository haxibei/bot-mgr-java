package com.ruoyi.common.core.domain;

public enum GlobalResultCode implements IResultCode {

    SUCCESS(200,  "成功"),
    FAIL(500,  "失败"),

    PARAM_ERROR(400,  "参数错误，请重新进入该页面再重试！"),
    
    DB_OPERATE_ERROR(500, "数据库操作错误，操作失败，请重试！"),

    LOGIN_ERR(400,  "用户名或密码错误！"),

    NOT_LOGIN(401, "对不起，请先登录！"),

    SESSION_EXPIRED(401, "会话已过期"),

    UNAUTHORIZED(401, "未授权"),

    NO_PRIVILEGE(403, "对不起，您没有该权限！"),

    NON_SECURE_REQUEST(403, "对不起，该请求不安全！"),

    ILLEGAL_STATE(403, "对不起，数据状态不可用"),

    NONE_RESOURCE(404, "请求资源不存在"),

    SYSTEM_ERROR(500,  "系统错误，操作失败，请重试！"),

    UNKNOWN_ERROR(500,  "对不起，服务器发生未知错误，请稍后重试！"),

    FLOW_CONTROL_LIMIT(500,  "系统繁忙，请稍后重试！"),

    DATA_COVERT_ERROR(500,  "数据转换异常"),

    EXISTS(500,  "对不起，数据已存在！"),

    NOT_EXISTS(500,  "对不起，数据不存在！"),

    OVER_MAX_LIMIT(500,  "对不起，超过限制大小！"),

    OPERATE_FAST(500,  "操作频繁，请稍后再试！"),

    ;

    private final int code;

    private final String msg;

    GlobalResultCode(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    @Override
    public int getCode() {
        return code;
    }

    @Override
    public String getMsg() {
        return msg;
    }

}
