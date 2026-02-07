package com.ruoyi.utils;

import java.util.HashMap;
import java.util.Map;

public class ThreadUtil {

    private final static String BOT_ID = "bot_id"; //当前机器人id

    private static ThreadLocal<Map<String, Object>> threadInfoMap = new ThreadLocal<Map<String, Object>>();

    public static void put(String key, Object val) {

        Map<String, Object> infoMap = threadInfoMap.get();
        if (infoMap == null) {
            synchronized (ThreadUtil.class) {
                infoMap = threadInfoMap.get();
                if (infoMap == null) {
                    infoMap = new HashMap<String, Object>();
                    threadInfoMap.set(infoMap);
                }
            }
        }

        infoMap.put(key, val);
    }

    public static void putIfAbsent(String key, Object val) {
        if (get(key) == null) {
            put(key, val);
        }
    }

    @SuppressWarnings("all")
    public static <T> T get(String key) {

        Map<String, Object> infoMap = threadInfoMap.get();

        return infoMap == null ? null : (T) infoMap.get(key);
    }

    public static void setBotId(String botId) {
        putIfAbsent(BOT_ID, botId);
    }

    public static String getBotId() {
        return (String) get(BOT_ID);
    }

    public static void clear() {
        threadInfoMap.remove();
    }
}
