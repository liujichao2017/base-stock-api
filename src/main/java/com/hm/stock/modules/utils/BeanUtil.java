package com.hm.stock.modules.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
@Slf4j
@Lazy(false)
public class BeanUtil {

    private static ApplicationContext applicationContext;

    public BeanUtil(ApplicationContext applicationContext) {
        BeanUtil.applicationContext = applicationContext;
    }

    public static <T> T getBean(Class<T> clazz) {
        if (isNull()) return null;
        return applicationContext.getBean(clazz);
    }

    public static Object getBean(String name) {
        if (isNull()) return null;
        return applicationContext.getBean(name);
    }

    private static boolean isNull() {
        if (Objects.isNull(applicationContext)) {
            log.error("applicationContext is null");
            return true;
        }
        return false;
    }
}
