package com.hm.stock.domain.site.aspect;

import com.alibaba.fastjson.JSONObject;
import com.hm.stock.domain.auth.config.UrlWhitelist;
import com.hm.stock.domain.auth.entity.SessionInfo;
import com.hm.stock.domain.site.entity.SiteAuditLog;
import com.hm.stock.domain.site.mapper.SiteAuditLogMapper;
import com.hm.stock.modules.common.LogicUtils;
import com.hm.stock.modules.enums.YNEnum;
import com.hm.stock.modules.execptions.InternalException;
import com.hm.stock.modules.utils.JsonUtil;
import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

@Aspect
@Component
@Slf4j
@Order(1)
public class AuditLogAspect {

    @Autowired
    private HttpServletRequest request;

    @Autowired
    private SiteAuditLogMapper siteAuditLogMapper;

    @Resource
    public UrlWhitelist whitelist;

    private static final String ADMIN_LOGIN_URL = "/admin/login";

    /**
     * 定义一个切点:所有被 GetMapping 注解修饰的方法都会被织入 advice
     */
    @Pointcut("@annotation(org.springframework.web.bind.annotation.PostMapping)"
            + "|| @annotation(org.springframework.web.bind.annotation.PutMapping)"
            + "|| @annotation(org.springframework.web.bind.annotation.DeleteMapping)")
    private void authPointcut() {

    }

    /**
     * 表示 logAdvice 将在目标方法执行前执行
     */
    @Around("authPointcut()")
    public Object logAdvice(ProceedingJoinPoint point) throws Throwable {
        String requestURI = request.getRequestURI();
        Signature signature = point.getSignature();
        MethodSignature methodSignature = (MethodSignature) signature;
        Method method = methodSignature.getMethod();
        Operation operation = method.getAnnotation(Operation.class);
        String name = requestURI;
        if (operation != null) name = operation.summary();


        SiteAuditLog siteAuditLog = new SiteAuditLog();
        siteAuditLog.setUrlName(name);
        siteAuditLog.setUrlMethod(request.getMethod());
        siteAuditLog.setUrlPath(requestURI);
        siteAuditLog.setReqBody("{}");
        siteAuditLog.setAddr(getIpAddr(request));

        try {
            SessionInfo instance = SessionInfo.getInstance();
            siteAuditLog.setMemberId(instance.getId());
        } catch (Exception e) {
            siteAuditLog.setMemberId(-1L);
        }


        Object proceed = null;
        long start = System.currentTimeMillis();
        try {
            Object[] args = point.getArgs();
            if (LogicUtils.isNotEmpty(args)) {
                siteAuditLog.setReqBody(JsonUtil.toFormatStr(exclude(args)));
            }

            proceed = point.proceed();

            siteAuditLog.setResBody(JsonUtil.toFormatStr(proceed));
            siteAuditLog.setStatus(YNEnum.YES.getType());
            if (LogicUtils.isEquals(ADMIN_LOGIN_URL, requestURI)) {
                JSONObject res, data;
                Long id;
                if ((res = JSONObject.parseObject(JsonUtil.toStr(proceed))) != null
                        && (data = res.getJSONObject("data")) != null
                        && (id = data.getLong("id")) != null) {
                    siteAuditLog.setMemberId(id);
                }
            }
        } catch (InternalException e) {
            siteAuditLog.setResBody(e.getResultCode().getMessage());
            siteAuditLog.setStatus(YNEnum.NO.getType());
            throw e;
        } catch (Throwable e) {
            siteAuditLog.setResBody(e.getMessage());
            siteAuditLog.setStatus(YNEnum.NO.getType());
            throw e;
        } finally {
            try {
                long end = System.currentTimeMillis();
                siteAuditLog.setTime(end - start);
                siteAuditLogMapper.insert(siteAuditLog);
            } catch (Exception e) {
                log.error("记录审计日志错误: {}", JsonUtil.toStr(siteAuditLog), e);
            }
        }
        return proceed;
    }

    private Object[] exclude(Object[] args) {
        List<Object> arr = new ArrayList<>();
        for (int i = 0; i < args.length; i++) {
            if (!(args[i] instanceof MultipartFile)) {
                arr.add(args[i]);
            }
        }
        Object[] objects = new Object[arr.size()];
        int count = 0;
        for (Object o : arr) {
            objects[count++] = o;
        }
        return objects;
    }


    /**
     * 获取客户端IP
     *
     * @param request 请求对象
     * @return IP地址
     */
    public static String getIpAddr(HttpServletRequest request) {
        if (request == null) {
            return "unknown";
        }
        String ip = request.getHeader("x-forwarded-for");
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("X-Forwarded-For");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("X-Real-IP");
        }

        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }

        return "0:0:0:0:0:0:0:1".equals(ip) ? "127.0.0.1" : getMultistageReverseProxyIp(ip);
    }

    /**
     * 从多级反向代理中获得第一个非unknown IP地址
     *
     * @param ip 获得的IP地址
     * @return 第一个非unknown IP地址
     */
    public static String getMultistageReverseProxyIp(String ip) {
        // 多级反向代理检测
        if (ip != null && ip.indexOf(",") > 0) {
            final String[] ips = ip.trim().split(",");
            for (String subIp : ips) {
                if (false == isUnknown(subIp)) {
                    ip = subIp;
                    break;
                }
            }
        }
        return substring(ip, 0, 255);
    }

    /**
     * 检测给定字符串是否为未知，多用于检测HTTP请求相关
     *
     * @param checkString 被检测的字符串
     * @return 是否未知
     */
    public static boolean isUnknown(String checkString) {
        return LogicUtils.isBlank(checkString) || "unknown".equalsIgnoreCase(checkString);
    }

    /**
     * 截取字符串
     *
     * @param str   字符串
     * @param start 开始
     * @param end   结束
     * @return 结果
     */
    public static String substring(final String str, int start, int end) {
        if (str == null) {
            return "";
        }

        if (end < 0) {
            end = str.length() + end;
        }
        if (start < 0) {
            start = str.length() + start;
        }

        if (end > str.length()) {
            end = str.length();
        }

        if (start > end) {
            return "";
        }

        if (start < 0) {
            start = 0;
        }
        if (end < 0) {
            end = 0;
        }

        return str.substring(start, end);
    }
}
