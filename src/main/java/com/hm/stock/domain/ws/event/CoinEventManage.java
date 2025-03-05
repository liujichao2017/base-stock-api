package com.hm.stock.domain.ws.event;

import com.hm.stock.domain.coin.entity.CoinSymbols;
import com.hm.stock.domain.ws.WsFuncConsts;
import com.hm.stock.domain.ws.entity.WsResult;
import com.hm.stock.domain.ws.handler.SocketSessionManage;
import com.hm.stock.modules.common.LogicUtils;
import com.hm.stock.modules.utils.ExecuteUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.websocket.Session;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Component
@Slf4j
public class CoinEventManage {
    @Autowired
    private SocketSessionManage sessionManage;

    // Map<symbol, Set<sessionId>>  股票订阅列表
    private final Map<String, Set<String>> SUBSCRIBE_LIST = new ConcurrentHashMap<>();

    // Map<sessionId, Set<symbol>>  会话监听列表
    private final Map<String, Set<String>> SESSION_LIST = new ConcurrentHashMap<>();


    public void subscribe(String sessionId, List<String> gids) {
        cancel(sessionId, null);
        for (String gid : gids) {
            SUBSCRIBE_LIST.computeIfAbsent(gid, k -> ConcurrentHashMap.newKeySet()).add(sessionId);
        }
        SESSION_LIST.computeIfAbsent(sessionId, k -> ConcurrentHashMap.newKeySet()).addAll(gids);
    }

    public void cancel(String sessionId, List<String> gids) {
        if (LogicUtils.isNotEmpty(gids)) {
            for (String gid : gids) {
                Set<String> sessionIds = SUBSCRIBE_LIST.get(gid);
                if (LogicUtils.isNotEmpty(sessionIds)) {
                    sessionIds.remove(sessionId);
                }
                if (LogicUtils.isEmpty(sessionIds)) {
                    SUBSCRIBE_LIST.remove(gid);
                }
            }
            return;
        }
        Set<String> gidSet = SESSION_LIST.remove(sessionId);
        if (LogicUtils.isNotEmpty(gidSet)) {
            for (String gid : gidSet) {
                Set<String> sessionIds = SUBSCRIBE_LIST.get(gid);
                if (LogicUtils.isNotEmpty(sessionIds)) {
                    sessionIds.remove(sessionId);
                }
                if (LogicUtils.isEmpty(sessionIds)) {
                    SUBSCRIBE_LIST.remove(gid);
                }
            }
        }
    }

    public void publish(List<CoinSymbols> coinSymbols) {
        for (CoinSymbols coinSymbol : coinSymbols) {
            if (SUBSCRIBE_LIST.containsKey(coinSymbol.getSymbol())) {
                ExecuteUtil.COIN.execute(() -> {
                    send(coinSymbol);
                });
            }
        }
    }

    public void publish(CoinSymbols coinSymbol) {
        ExecuteUtil.COIN.execute(() -> {
            send(coinSymbol);
        });
    }


    private void send(CoinSymbols coinSymbol) {
        Set<String> sessionIds = SUBSCRIBE_LIST.get(coinSymbol.getSymbol());
        if (LogicUtils.isEmpty(sessionIds)) {
            return;
        }
        for (String sessionId : sessionIds) {
            Session session = sessionManage.getSession(sessionId);
            sessionManage.send(session, WsResult.get(WsFuncConsts.STOCK, coinSymbol));
        }
    }
}
