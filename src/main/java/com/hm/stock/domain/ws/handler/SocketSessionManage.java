package com.hm.stock.domain.ws.handler;

import com.hm.stock.domain.auth.entity.SessionInfo;
import com.hm.stock.domain.ws.WsFuncConsts;
import com.hm.stock.domain.ws.entity.WsMember;
import com.hm.stock.domain.ws.entity.WsResult;
import com.hm.stock.domain.ws.event.CoinEventManage;
import com.hm.stock.domain.ws.event.StockEventManage;
import com.hm.stock.modules.common.LogicUtils;
import com.hm.stock.modules.execptions.CommonResultCode;
import com.hm.stock.modules.execptions.InternalException;
import com.hm.stock.modules.utils.BeanUtil;
import com.hm.stock.modules.utils.JsonUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.websocket.Session;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Component
@Slf4j
public class SocketSessionManage {

    @Autowired
    private StockEventManage stockEventManage;
    @Autowired
    private CoinEventManage coinEventManage;

    private final Map<String, Session> sessionMap = new ConcurrentHashMap<>();
    private final Map<Long, List<String>> memberIdMap = new ConcurrentHashMap<>();
    private final Map<String, Long> sessionIdMap = new ConcurrentHashMap<>();

    public WsResult connect(Session session, String token) {
        try {
            SessionInfo sessionInfo = SessionInfo.parse(token);
            Long id = sessionInfo.getId();
            memberIdMap.computeIfAbsent(id, key -> new ArrayList<>()).add(session.getId());
            sessionMap.put(session.getId(), session);
            sessionIdMap.put(session.getId(), id);

            return WsResult.get(WsFuncConsts.COMMON);
        } catch (InternalException e) {
            log.error("ws链接内部异常: {}\n", e.getResultCode().getMessage());
            return WsResult.get(WsFuncConsts.COMMON, e.getResultCode());
        } catch (Exception e) {
            log.error("ws链接未知异常: \n", e);
            return WsResult.get(WsFuncConsts.COMMON, CommonResultCode.INTERNAL_ERROR);
        }
    }

    public WsResult check(Session session, String token) {
        try {
            SessionInfo sessionInfo = SessionInfo.parse(token);
            Long memberId = sessionIdMap.get(session.getId());
            LogicUtils.assertNotNull(memberId, CommonResultCode.LOGIN_ERROR);
            LogicUtils.assertEquals(memberId, sessionInfo.getId(), CommonResultCode.LOGIN_ERROR);
            return WsResult.get(WsFuncConsts.COMMON);
        } catch (InternalException e) {
            log.error("ws链接内部异常: {}\n", e.getResultCode().getMessage());
            return WsResult.get(WsFuncConsts.COMMON, e.getResultCode());
        } catch (Exception e) {
            log.error("ws链接未知异常: \n", e);
            return WsResult.get(WsFuncConsts.COMMON, CommonResultCode.INTERNAL_ERROR);
        }
    }


    public Long getMemberId(Session session) {
        return sessionIdMap.get(session.getId());
    }

    public Session getSession(String id) {
        Session session = sessionMap.get(id);
        if (session == null) {
            close(id);
            return null;
        }
        if (session.isOpen()) {
            return session;
        }
        close(session);
        return null;
    }

    public List<Session> getSession(Long id) {
        List<String> sessionIds = memberIdMap.get(id);
        if (LogicUtils.isEmpty(sessionIds)) {
            return Collections.emptyList();
        }
        return sessionIds.stream().map(this::getSession).filter(LogicUtils::isNotNull).collect(Collectors.toList());
    }

    public void close(Session session) {
        if (LogicUtils.isNull(session)) {
            return;
        }
        try {
            if (session.isOpen()) {
                session.close();
            }
        } catch (Exception e) {
            log.error("关闭ws会话失败: \n", e);
        }
        String id = session.getId();
        sessionMap.remove(id);
        Long memberId = sessionIdMap.remove(id);
        if (LogicUtils.isNotNull(memberId)) {
            List<String> sessionIds = memberIdMap.get(memberId);
            if (LogicUtils.isNotEmpty(sessionIds)) {
                sessionIds.remove(id);
                if (LogicUtils.isEmpty(sessionIds)) {
                    memberIdMap.remove(memberId);
                }
            }
        }
        stockEventManage.cancel(id, null);
        coinEventManage.cancel(id, null);
    }

    public void close(String sessionId) {
        String id = sessionId;
        sessionMap.remove(id);
        Long memberId = sessionIdMap.remove(id);
        if (LogicUtils.isNotNull(memberId)) {
            List<String> sessionIds = memberIdMap.get(memberId);
            if (LogicUtils.isNotEmpty(sessionIds)) {
                sessionIds.remove(id);
                if (LogicUtils.isEmpty(sessionIds)) {
                    memberIdMap.remove(memberId);
                }
            }
        }
        stockEventManage.cancel(id, null);
        coinEventManage.cancel(id, null);
    }

    public void send(Session session, WsResult result) {
        SocketSessionManage sessionManage = BeanUtil.getBean(SocketSessionManage.class);
        if (session.isOpen()) {
            try {
                session.getBasicRemote().sendText(JsonUtil.toStr(result));
            } catch (IOException e) {
                log.error("回复消息失败: memberId:{}, sessionId: {}, 回复消息: {}", sessionManage.getMemberId(session),
                        session.getId(), JsonUtil.toStr(result));
                BeanUtil.getBean(SocketSessionManage.class).close(session);
            }
        } else {
            sessionManage.close(session);
        }
    }

    public void send(Long memberId, WsMember wsMember) {
        List<Session> sessions = getSession(memberId);
        if (LogicUtils.isEmpty(sessions)) {
            return;
        }
        for (Session session : sessions) {
            send(session, WsResult.get("member.info", wsMember));
        }
    }
}
