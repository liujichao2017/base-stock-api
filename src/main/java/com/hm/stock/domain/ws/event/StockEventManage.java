package com.hm.stock.domain.ws.event;

import com.hm.stock.domain.stock.entity.Stock;
import com.hm.stock.domain.stock.service.StockService;
import com.hm.stock.domain.ws.WsFuncConsts;
import com.hm.stock.domain.ws.entity.WsResult;
import com.hm.stock.domain.ws.handler.SocketSessionManage;
import com.hm.stock.modules.common.LogicUtils;
import com.hm.stock.modules.utils.ExecuteUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.websocket.Session;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

@Component
@Slf4j
public class StockEventManage {

    @Autowired
    private SocketSessionManage sessionManage;

    @Autowired
    private StockService service;

    // Map<StockGid, Set<sessionId>>  股票订阅列表
    private final Map<String, Set<String>> SUBSCRIBE_LIST = new ConcurrentHashMap<>();

    private final Map<String, BigDecimal> STOCK_LIST = new ConcurrentHashMap<>();

    // Map<sessionId, Set<StockGid>>  会话监听列表
    private final Map<String, Set<String>> SESSION_LIST = new ConcurrentHashMap<>();


    {
        new Thread(this::timer).start();
    }

    private void timer() {
        while (true) {
            try {
                if (LogicUtils.isEmpty(SESSION_LIST)) {
                    SUBSCRIBE_LIST.clear();
                }
                if (LogicUtils.isNotEmpty(SUBSCRIBE_LIST)) {
                    for (String gid : SUBSCRIBE_LIST.keySet()) {
                        ExecuteUtil.STOCK.execute(() -> {
                            Stock stock = service.getStockByWs(gid);
                            BigDecimal oldLast = STOCK_LIST.getOrDefault(stock.getGid(), BigDecimal.ZERO);
                            if (stock.getLast().compareTo(oldLast) != 0) {
                                STOCK_LIST.put(stock.getGid(), stock.getLast());
                                send(stock);
                            }
                        });
                    }
                    log.info("会话数量: {},订阅股票数量: {}", SESSION_LIST.size(), SUBSCRIBE_LIST.size());
                }
                TimeUnit.SECONDS.sleep(30);
            } catch (Exception e) {
                log.error("WS股票代码同步器异常", e);
            }
        }
    }

    public void subscribe(String sessionId, List<String> gids) {
        cancel(sessionId, null);
        for (String gid : gids) {
            if (LogicUtils.isNotNull(gid)) {
                Set<String> subList = SUBSCRIBE_LIST.computeIfAbsent(gid, k -> ConcurrentHashMap.newKeySet());
                subList.add(sessionId);
            }
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
                    STOCK_LIST.remove(gid);
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
                    STOCK_LIST.remove(gid);
                }
            }
        }
    }

    public void publish(List<Stock> stocks) {
        for (Stock stock : stocks) {
            if (SUBSCRIBE_LIST.containsKey(stock.getGid())) {
                ExecuteUtil.STOCK.execute(() -> {
                    send(stock);
                });
            }
        }
    }

    private void send(Stock stock) {
        Set<String> sessionIds = SUBSCRIBE_LIST.get(stock.getGid());
        if (LogicUtils.isEmpty(sessionIds)) {
            return;
        }
        for (String sessionId : sessionIds) {
            Session session = sessionManage.getSession(sessionId);
            sessionManage.send(session, WsResult.get(WsFuncConsts.STOCK, stock));
        }
    }

}
