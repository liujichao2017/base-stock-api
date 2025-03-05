package com.hm.stock.domain.ws.handler;

import com.hm.stock.domain.ws.WsFuncConsts;
import com.hm.stock.domain.ws.entity.WsParam;
import com.hm.stock.domain.ws.entity.WsResult;
import com.hm.stock.modules.common.LogicUtils;
import com.hm.stock.modules.execptions.CommonResultCode;
import com.hm.stock.modules.execptions.InternalException;
import com.hm.stock.modules.utils.BeanUtil;
import com.hm.stock.modules.utils.JsonUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.websocket.Session;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

@Component
@Slf4j
public class WsFunctionHandler {

    @Autowired
    private SocketSessionManage sessionManage;

    public void execute(String message, Session session) {
        try {

            if ("ping".equals(message)) {
                return;
            }

            WsParam param = JsonUtil.toObj(message, WsParam.class);
            LogicUtils.assertNotNull(param, CommonResultCode.AUT_ERROR);
            LogicUtils.assertNotNull(param.getFunc(), CommonResultCode.AUT_ERROR);
            String func = param.getFunc();
            String[] split = func.split("\\.");
            LogicUtils.assertTrue(split.length == 2, CommonResultCode.AUT_ERROR);
            String beanName = split[0];
            String methodName = split[1];
            Object bean = BeanUtil.getBean(beanName);
            Method method = bean.getClass().getMethod(methodName, String.class, String.class);
            LogicUtils.assertNotNull(method, CommonResultCode.AUT_ERROR);
            method.invoke(bean, session.getId(),
                    param.getParam() == null ? null : JsonUtil.toStr(param.getParam()));
        } catch (InternalException e) {
            log.error("ws链接内部异常: {}\n", e.getResultCode().getMessage());
            sessionManage.send(session, WsResult.get(WsFuncConsts.COMMON, e.getResultCode()));
        } catch (InvocationTargetException e) {
            if (e.getTargetException() instanceof InternalException) {
                InternalException targetException = (InternalException) e.getTargetException();
                log.error("ws链接内部异常:res:{} {}\n", message, targetException.getResultCode().getMessage());
                sessionManage.send(session, WsResult.get(WsFuncConsts.COMMON, targetException.getResultCode()));
                return;
            }
            log.error("ws链接未知异常:{} \n", message, e);
            sessionManage.send(session, WsResult.get(WsFuncConsts.COMMON, CommonResultCode.INTERNAL_ERROR));
        } catch (Exception e) {
            log.error("ws链接未知异常:{} \n", message, e);
            sessionManage.send(session, WsResult.get(WsFuncConsts.COMMON, CommonResultCode.INTERNAL_ERROR));
        }
    }
}
