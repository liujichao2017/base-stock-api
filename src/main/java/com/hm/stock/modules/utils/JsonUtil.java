package com.hm.stock.modules.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JsonUtil {

    private static final Logger log = LoggerFactory.getLogger(JsonUtil.class);

    public static String toStr(Object obj) {
        return JSONObject.toJSONString(obj);
    }

    public static String toFormatStr(Object obj) {
        return JSONObject.toJSONString(obj, SerializerFeature.PrettyFormat);
    }

    public static String toFormatStr(String obj) {
        return JSONObject.toJSONString(JSONObject.parse(obj), SerializerFeature.PrettyFormat);
    }

    public static <T> T toObj(String val, Class<T> tClass) {
        try {
            return JSON.parseObject(val, tClass);
        } catch (RuntimeException e) {
            log.error("解析JSON异常. json: {}", val);
            throw e;
        }
    }
}
