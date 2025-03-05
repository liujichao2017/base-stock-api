package com.hm.stock.modules.redis;

import java.util.concurrent.TimeUnit;

public class RedisKey {

    public static String tokenKey(String key) {
        return "app:token:" + key;
    }

    public static String tokenByIdKey(Long key) {
        return "app:id:" + key;
    }

    public static String klineKey(String stockGid, String type) {
        return "kline:" + type + ":" + stockGid;
    }

    public static String smsCodeKey(String phone) {
        return "sms:code:" + phone;
    }
}
