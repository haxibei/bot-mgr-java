package com.ruoyi.controller;

import com.ruoyi.common.redis.service.RedisService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

@RestController
@Slf4j
public class PingController {

    @Value("${spring.application.name}")
    private String name;

    @Value("${server.port}")
    private String port;

    @Autowired
    private DataSourceProperties dataSourceProperties;

    @Autowired
    private RedisProperties redisProperties;

    @Autowired
    private RedisService redisService;

    @RequestMapping("/ping")
    public PongData ping() {

        String msg = "";
        int state = 1;

        //只返回异常的信息
        String hostName = "";
        try {
            hostName = InetAddress.getLocalHost().toString();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        msg += "应用： " + name + " " + hostName + ":" + port + "\n";
        // 检查 redis
        try {
            redisService.ping();
        } catch (Exception e) {
            msg += "redis： " + redisProperties.getHost() + ":" + redisProperties.getPort() + " 连接异常: " + e.getMessage() + "\n";
            state = 0;
        }
        // 检查 database
        String url = dataSourceProperties.getUrl();
        String[] parts = url.split("//")[1].split("/")[0].split(":");

        Connection conn = null;
        Statement stmt = null;
        try {
            conn = DriverManager.getConnection(url, dataSourceProperties.getUsername(), dataSourceProperties.getPassword());
            stmt = conn.createStatement();
        } catch (SQLException e) {
            String ip = parts[0];
            String port = parts[1];

            msg += "mysql： " + ip + ":" + port + "连接异常: " + e.getMessage() + "\n";
            state = 0;
        }finally {
           if(conn != null) {
               try {
                   conn.close();
               } catch (SQLException e) {
                   throw new RuntimeException(e);
               }
           }
            if (stmt != null) {
                try { stmt.close(); } catch (SQLException ignored) {}
            }
        }

        if(state == 1) {
            msg += "服务正常";
        }
        return new PongData(state, msg);
    }

    @Data
    @AllArgsConstructor
    class PongData{
        //1 正常  0 有异常
        private int state;
        //描述
        private String msg;
    }
}


