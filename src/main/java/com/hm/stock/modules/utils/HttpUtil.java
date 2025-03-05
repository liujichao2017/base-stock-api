package com.hm.stock.modules.utils;

import com.hm.stock.modules.common.Result;
import com.hm.stock.modules.execptions.ResultCode;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.io.IOException;


public class HttpUtil {

    public static HttpServletRequest getRequest(){
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) requestAttributes;
        return servletRequestAttributes.getRequest();
    }

    public static HttpServletResponse getResponse(){
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) requestAttributes;
        return servletRequestAttributes.getResponse();
    }

    public static String getHeader(String name){
        return getRequest().getHeader(name);
    }

    public static void writerErrMsg(ResultCode resultCode) throws IOException {
        HttpServletResponse response = getResponse();
        setCorsHeader(response);
        response.setStatus(200);
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write(JsonUtil.toStr(Result.error(resultCode)));
    }

    public static void setCorsHeader(HttpServletResponse response) {
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Allow-Methods", "POST,OPTIONS,GET,PUT,DELETE");
        response.setHeader("Access-Control-Max-Age", "3600");
        response.setHeader("Access-Control-Allow-Headers", "*");
        response.setHeader("Access-Control-Allow-Credentials", "true");
    }

}
