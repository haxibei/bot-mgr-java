package com.ruoyi.common.core.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class BasePropConfig {

	@Value("${crypt.aesKey:e54d79b79981447d}")
	private String aesKey;

	public String getAesKey() {
		return aesKey;
	}

}
