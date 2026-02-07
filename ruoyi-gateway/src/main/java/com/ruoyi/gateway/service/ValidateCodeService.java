package com.ruoyi.gateway.service;

import java.io.IOException;

import com.ruoyi.common.core.domain.R;
import com.ruoyi.common.core.exception.CaptchaException;

/**
 * 验证码处理
 *
 * @author ruoyi
 */
public interface ValidateCodeService
{
    /**
     * 生成验证码
     */
    public R createCaptcha() throws IOException, CaptchaException;

    /**
     * 校验验证码
     */
    public void checkCaptcha(String key, String value) throws CaptchaException;
}
