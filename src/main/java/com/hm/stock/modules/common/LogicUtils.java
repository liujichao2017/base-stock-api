package com.hm.stock.modules.common;

import cn.hutool.core.lang.Snowflake;
import cn.hutool.core.util.IdUtil;
import com.hm.stock.modules.execptions.InternalException;
import com.hm.stock.modules.execptions.ResultCode;
import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;
import java.security.SecureRandom;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Function;

public class LogicUtils {
    /**
     * 期待传入的参数 为 true
     *
     * @param flag       传入的参数
     * @param resultCode 不为true 则抛出异常
     */
    public static void assertTrue(boolean flag, ResultCode resultCode) {
        if (!flag) {
            throw new InternalException(resultCode);
        }
    }

    /**
     * 期待传入的参数 为 true
     *
     * @param flag       传入的参数
     * @param resultCode 不为true 则抛出异常
     */
    public static void assertTrue(boolean flag, ResultCode resultCode, Object... param) {
        if (!flag) {
            throw new InternalException(resultCode,param);
        }
    }

    public static void assertFalse(boolean flag, ResultCode resultCode) {
        if (flag) {
            throw new InternalException(resultCode);
        }
    }

    public static void assertNotNull(Object obj, ResultCode resultCode) {
        if (isNull(obj)) {
            throw new InternalException(resultCode);
        }
    }

    public static void assertNotBlank(String obj, ResultCode resultCode) {
        if (isBlank(obj)) {
            throw new InternalException(resultCode);
        }
    }

    public static void assertNotEmpty(List<?> obj, ResultCode resultCode) {
        if (isEmpty(obj)) {
            throw new InternalException(resultCode);
        }
    }

    public static boolean isNotBlank(String obj) {
        return !isBlank(obj);
    }

    public static boolean isNotNull(Object obj) {
        return !isNull(obj);
    }

    public static boolean isBlank(String obj) {
        return isNull(obj) || obj.trim().isEmpty();
    }

    public static boolean isNotEmpty(Collection<?> arr) {
        return !isEmpty(arr);
    }

    public static boolean isNotEmpty(Map<?, ?> map) {
        return !isEmpty(map);
    }

    public static boolean isEmpty(Map<?, ?> map) {
        return isNull(map) || map.isEmpty();
    }

    public static boolean isEmpty(Collection<?> arr) {
        return isNull(arr) || arr.isEmpty();
    }

    public static boolean isNotEmpty(Object[] arr) {
        return !isEmpty(arr);
    }

    public static boolean isEmpty(Object[] arr) {
        return isNull(arr) || arr.length == 0;
    }

    public static boolean isNull(Object obj) {
        return obj == null;
    }

    public static String join(List<String> list, String separator) {
        return StringUtils.join(list, separator);
    }


    public static void assertEquals(Object obj1, Object obj2, ResultCode resultCode) {
        if (isNotEquals(obj1, obj2)) {
            throw new InternalException(resultCode);
        }
    }

    public static void assertNotEquals(Object obj1, Object obj2, ResultCode resultCode) {
        if (isEquals(obj1, obj2)) {
            throw new InternalException(resultCode);
        }
    }

    public static boolean isNotEquals(Object obj1, Object obj2) {
        return !isEquals(obj1, obj2);
    }

    public static boolean isEquals(Object obj1, Object obj2) {
        return Objects.equals(obj1, obj2);
    }

    public static boolean isNotEquals(BigDecimal obj1, BigDecimal obj2) {
        return !isEquals(obj1, obj2);
    }

    public static boolean isEquals(BigDecimal obj1, BigDecimal obj2) {
        return (obj1 == null && obj2 == null) || (obj1 != null && obj2 != null && obj1.compareTo(obj2) == 0);
    }

    private static final Snowflake snowflake = IdUtil.createSnowflake(1, 1);

    public static String getOrderSn(String model) {
        long l = snowflake.nextId();
        return model + l;
    }

    public static boolean isNumber(String num) {
        try {
            double v = Double.parseDouble(num);
        } catch (NumberFormatException e) {
            return false;
        }
        return true;
    }

    public static BigDecimal getRandom(BigDecimal min, BigDecimal max) {
        if (min.compareTo(max) == 0) {
            return min;
        }
        BigDecimal res = min.add(new BigDecimal(Math.random()).multiply(max.subtract(min)));
        res = res.setScale(Math.max(min.scale(), max.scale()), 4);
        if (res.compareTo(BigDecimal.ZERO) == 0) {
            return min;
        }
        return res;
    }

    public static int getRandom(int min, int max) {
        if (min == max) {
            return min;
        }
        return ThreadLocalRandom.current().nextInt(min, max);
    }

    public static String getRandomNumber(int count) {
        StringBuilder sb = new StringBuilder();
        SecureRandom random = new SecureRandom();
        for (int i = 0; i < count; i++) {
            sb.append(random.nextInt(9) + (i == 0 ? 1 : 0));
        }
        return sb.toString();
    }

    public static <V, T> Map<V, List<T>> groupMap(List<T> list, Function<T, V> function) {
        Map<V, List<T>> map = new HashMap<>();
        for (T t : list) {
            map.computeIfAbsent(function.apply(t), key -> new ArrayList<>()).add(t);
        }
        return map;
    }

    public static boolean isAny(Object status, Object... val) {
        for (Object v : val) {
            if (isEquals(status, v)) {
                return true;
            }
        }
        return false;
    }
}
