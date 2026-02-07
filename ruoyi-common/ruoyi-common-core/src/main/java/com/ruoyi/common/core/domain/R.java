package com.ruoyi.common.core.domain;

import lombok.Data;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * 响应信息主体
 *
 * @author ruoyi
 */
@Data
public class  R<T> implements Serializable
{
    private static final long serialVersionUID = 1L;


    private int code;

    private String msg;

    private T data;

    private Map<String, Object> extendData;

    public void addExtendData(String key, Object val) {
        if(extendData == null) {
            extendData = new HashMap<>();
        }
        extendData.put(key, val);
    }

    public R() {
        this(GlobalResultCode.SUCCESS);
    }

    public R(GlobalResultCode code, String msg) {
        this(null, code.getCode(), msg);
    }

    public R(int code, String msg) {
        this(null, code, msg);
    }

    public R(IResultCode result) {
        this(null, result.getCode(), result.getMsg());
    }

    public R(T data, int code, String msg) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    public R(T data) {
        this.data = data;

        if (!(this.data instanceof Boolean) || (Boolean) (this.data)) {
            this.code = GlobalResultCode.SUCCESS.getCode();
            this.msg = GlobalResultCode.SUCCESS.getMsg();
        }
    }

    public static R ok()
    {
        return new R(GlobalResultCode.SUCCESS);
    }

    public static <T> R<T> ok(T data)
    {
        return new R(data);
    }

    public static <T> R<T> ok(T data, String msg)
    {
        return new R(data, GlobalResultCode.SUCCESS.getCode(), msg);
    }

    public static <T> R<T> fail()
    {
        return new R(GlobalResultCode.FAIL);
    }

    public static <T> R<T> fail(String msg)
    {
        return fail(null, msg);
    }

    public static <T> R<T> fail(T data)
    {
        return fail(data, GlobalResultCode.FAIL.getMsg());
    }

    public static <T> R<T> fail(T data, String msg)
    {
        return new R(data, GlobalResultCode.FAIL.getCode(), msg);
    }

    public static <T> R<T> fail(int code, T data, String msg)
    {
        return new R(data, code, msg);
    }

    public static <T> R<T> fail(int code, String msg)
    {
        return new R(null, code, msg);
    }


    public static <T> Boolean error(R<T> ret)
    {
        return !success(ret);
    }

    public static <T> Boolean success(R<T> ret)
    {
        return GlobalResultCode.SUCCESS.getCode() == ret.getCode();
    }
}
