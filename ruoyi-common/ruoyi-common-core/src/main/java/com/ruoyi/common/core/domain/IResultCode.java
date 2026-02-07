package com.ruoyi.common.core.domain;

public interface IResultCode {
    /**
     * @return 错误码或者异常码
     */
    int getCode();

    /**
     * @return 返回程序员能看懂的消息提示，以便查找原因
     */

    String getMsg();

}
