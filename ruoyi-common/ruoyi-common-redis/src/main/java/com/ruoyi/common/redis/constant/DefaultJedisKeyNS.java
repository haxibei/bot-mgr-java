package com.ruoyi.common.redis.constant;

public enum DefaultJedisKeyNS implements RedisKeyNS {
    token("TOKEN", 7 * 24 * 60 * 60, "登录信息"),

    auth("AUTH", 10 * 60, "缓存密码错误信息"),

    ip_black("IP_BLACK", -1, "ip黑名单"),

    dict("DICT", -1, "字典"),

    distlock("GLOBAL:LOCK:SECOND3",3, "模拟分布式资源锁，3秒内某个key获取到的数值为0，则获得锁立马执行；否则需要等待锁释放或者该key超过失效时间(3秒)，该锁不能用于耗时较长的操作"),
    distlock_3min("GLOBAL:LOCK:SECOND180",3 * 60, "模拟分布式资源锁，3分内某个key获取到的数值为0，则获得锁立马执行；否则需要等待锁释放或者该key超过失效时间(3分)，该锁不能用于耗时较长的操作"),

    global_config("GLOBAL:CONFIG",-1, "全局配置"),

    tg_wait_input("TG_WAIT_INPUT", 1 * 60,"telegram当前等待输入的命令"),

    tg_next_msg("TG_NEXT_MSG",12 * 60 * 60,"telegram 消息关联的下一条消息"),

    tg_msg_lock("TG_LOCKED_MSG",2,"telegram 消息关联的下一条消息"),
    verify_code("VERIFY_CODE",2 * 60 ,"验证码" ),
    ;

    private final String nameSpace;

    private final int expireTime;
    private final String desc;
    private final boolean needRemoveAllCacheAfterModify;

    private DefaultJedisKeyNS(String nameSpace, int expireTime, String desc) {
        this.nameSpace = nameSpace;
        this.expireTime = expireTime;
        this.desc = desc;
        this.needRemoveAllCacheAfterModify = false;
    }

    @Override
    public String getNameSpace() {
        return this.nameSpace;
    }
    @Override
    public int getExpire() {
        return this.expireTime;
    }

    @Override
    public boolean needRemoveAllCacheAfterModify() {
        return this.needRemoveAllCacheAfterModify;
    }

    public String getDesc() {
        return desc;
    }

}
