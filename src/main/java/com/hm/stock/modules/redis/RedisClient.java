package com.hm.stock.modules.redis;

import com.hm.stock.modules.utils.JsonUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
public class RedisClient {
    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    public boolean set(String key, String value) {
        stringRedisTemplate.opsForValue().set(key, value);
        return true;
    }

    public boolean set(String key, String value, long timeout) {
        stringRedisTemplate.opsForValue().set(key, value, timeout, TimeUnit.SECONDS);
        return true;
    }

    public boolean set(String key, String value, TimeUnit timeUnit, long timeout) {
        stringRedisTemplate.opsForValue().set(key, value, timeout, timeUnit);
        return true;
    }

    public boolean set(String key, Object value, TimeUnit timeUnit, long timeout) {
        stringRedisTemplate.opsForValue().set(key, JsonUtil.toStr(value), timeout, timeUnit);
        return true;
    }

    public String get(String key) {
        String val = stringRedisTemplate.opsForValue().get(key);
        if ("null".equals(val)) {
            val = null;
        }
        return val;
    }

    public boolean del(String key) {
        Boolean delete = stringRedisTemplate.delete(key);
        return delete != null && delete;
    }

    public <T> T get(String key, Class<T> tClass) {
        String val = stringRedisTemplate.opsForValue().get(key);
        if ("null".equals(val)) {
            return null;
        }

        return JsonUtil.toObj(val, tClass);
    }

    public void addBySet(String key, String val) {
        stringRedisTemplate.opsForSet().add(key, val);
        stringRedisTemplate.expire(key, 2, TimeUnit.DAYS);
    }

    public Long setLen(String key) {
        Long size = stringRedisTemplate.opsForSet().size(key);
        return size == null ? 0L : size;
    }

}
