package com.ruoyi.exception;

import com.ruoyi.constant.ErrorMsgType;
import lombok.Data;

@Data
public class TgMsgException extends Exception{

    private ErrorMsgType errorMsgType;

    public TgMsgException(ErrorMsgType msgType, String message) {
        super(message);
        this.errorMsgType = msgType;
    }

}
