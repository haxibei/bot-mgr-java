package com.ruoyi;

import com.ruoyi.common.security.annotation.EnableRyFeignClients;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@MapperScan({"com.ruoyi.**.mapper", "com.ruoyi.common.mp.dao"})
@EnableScheduling
@EnableRyFeignClients
public class TgPushMsgApplication
{
    public static void main( String[] args )
    {
        try {
            SpringApplication.run(TgPushMsgApplication.class, args);
            System.out.println("(♥◠‿◠)ﾉﾞ  服务模块启动成功   ლ(´ڡ`ლ)ﾞ  \n");
        }catch (Exception e) {
            e.printStackTrace();
        }

    }
}
