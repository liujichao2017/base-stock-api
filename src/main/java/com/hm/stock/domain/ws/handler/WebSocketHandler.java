package com.hm.stock.domain.ws.handler;

import com.hm.stock.domain.ws.entity.WsResult;
import com.hm.stock.domain.ws.event.CoinEventManage;
import com.hm.stock.modules.common.LogicUtils;
import com.hm.stock.modules.execptions.CommonResultCode;
import com.hm.stock.modules.utils.BeanUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;

@Slf4j
@Component
@ServerEndpoint("/ws/connect/{token}")
public class WebSocketHandler {


    @OnOpen
    public void onOpen(Session session, @PathParam("token") String token) {
        SocketSessionManage sessionManage = BeanUtil.getBean(SocketSessionManage.class);
        WsResult res = sessionManage.connect(session, token);
        sessionManage.send(session, res);
    }

    @OnClose
    public void onClose(Session session) {
        SocketSessionManage sessionManage = BeanUtil.getBean(SocketSessionManage.class);
        sessionManage.close(session);
    }

    @OnMessage
    public void onMessage(String message, Session session, @PathParam("token") String token) {
        SocketSessionManage sessionManage = BeanUtil.getBean(SocketSessionManage.class);
        WsFunctionHandler functionHandler = BeanUtil.getBean(WsFunctionHandler.class);
        WsResult check = sessionManage.check(session, token);
        if (LogicUtils.isNotEquals(check.getCode(), CommonResultCode.OK.getCode())) {
            sessionManage.send(session, check);
        }
        functionHandler.execute(message, session);
    }

    @OnError
    public void onError(Session session, Throwable error) {
        SocketSessionManage sessionManage = BeanUtil.getBean(SocketSessionManage.class);
        log.error("会话发生未知异常: memberId:{}, sessionId: {}", sessionManage.getMemberId(session), session.getId(),
                error);
        sessionManage.close(session);
    }
}
