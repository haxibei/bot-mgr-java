package com.ruoyi.common.core.utils;

import com.ruoyi.common.core.config.BasePropConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 加密解密
 */
@Component
public final class DataCryptUtils {

    @Autowired
    private BasePropConfig basePropConfig;

    public String encryptData(String val) {
        return EncryptTools.encryptByAES(val, basePropConfig.getAesKey());
    }

    public String decryptData(String val) {
        return EncryptTools.decryptByAES(val, basePropConfig.getAesKey());
    }

    public static void main(String[] args) {
        System.out.println(EncryptTools.encryptByAES("ceshi@gmail.com", "e54d79b79981447d"));
    }
}
