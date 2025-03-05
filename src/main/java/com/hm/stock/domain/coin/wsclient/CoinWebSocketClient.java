package com.hm.stock.domain.coin.wsclient;

import cn.hutool.extra.spring.SpringUtil;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.hm.stock.domain.coin.entity.CoinSymbols;
import com.hm.stock.domain.coin.mapper.CoinSymbolsMapper;
import com.hm.stock.domain.coin.service.CoinDelegationService;
import com.hm.stock.domain.coin.service.CoinSymbolsService;
import com.hm.stock.domain.coin.utils.GZipUtils;
import com.hm.stock.domain.ws.event.CoinEventManage;
import com.hm.stock.modules.common.LogicUtils;
import com.hm.stock.modules.utils.ExecuteUtil;
import lombok.extern.slf4j.Slf4j;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.enums.ReadyState;
import org.java_websocket.handshake.ServerHandshake;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.net.URI;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
@ConditionalOnProperty(prefix = "coin", name = "enable", havingValue = "true", matchIfMissing = false)
public class CoinWebSocketClient extends WebSocketClient {

    @Resource
    private CoinSymbolsMapper coinSymbolsMapper;

    @Autowired
    private CoinEventManage coinEventManage;


    @Autowired
    private CoinDelegationService coinDeliveryService;

    public CoinWebSocketClient(URI serverUri) {
        super(serverUri);
    }

    public CoinWebSocketClient() {
        super(URI.create("wss://api-aws.huobi.pro/ws"));
        new Thread(() -> {
            while (true) {
                try {
                    watch();
                    TimeUnit.SECONDS.sleep(5);
                } catch (Exception e) {
                    log.error("监听火币连接异常: ", e);
                }

            }
        }).start();
    }

    @Override
    public void onOpen(ServerHandshake serverHandshake) {
        log.info("建立链接成功, 开始订阅火币");
        SpringUtil.getBean(CoinSymbolsService.class).subscribeSymbols(this, true);
    }

    @Override
    public void onMessage(String s) {
        if (s.contains("ping")) {
            JSONObject obj = JSONObject.parseObject(s);
            sendPong(obj.getLong("ping"));
            return;
        }
        ExecuteUtil.COIN_WX.execute(() -> {
            log.debug("接收数据: {}", s);
            JSONObject obj = JSONObject.parseObject(s);
            String ch = obj.getString("ch");
            if (LogicUtils.isBlank(ch)) {
                return;
            }
            JSONObject tick = obj.getJSONObject("tick");

            String[] split = ch.split("\\.");
            String symbol = split[1];

            CoinSymbols entity = buildSymbols(tick);
            QueryWrapper<CoinSymbols> ew = new QueryWrapper<>();
            ew.eq("symbol", symbol);
            coinSymbolsMapper.update(entity, ew);

            ExecuteUtil.COIN_TRADE.execute(() -> {
                coinDeliveryService.trigger(symbol, entity.getPrice());
            });

            entity.setSymbol(symbol);
            coinEventManage.publish(entity);
        });


    }

    private static @NotNull CoinSymbols buildSymbols(JSONObject tick) {
        CoinSymbols entity = new CoinSymbols();

        entity.setPrice(tick.getBigDecimal("lastPrice"));
        entity.setOpen(tick.getBigDecimal("open"));
        entity.setClose(tick.getBigDecimal("close"));
        entity.setAmount(tick.getBigDecimal("amount"));
        entity.setCounts(tick.getBigDecimal("count"));
        entity.setLow(tick.getBigDecimal("low"));
        entity.setHigh(tick.getBigDecimal("high"));
        entity.setVol(tick.getBigDecimal("vol"));
        entity.setBid(tick.getBigDecimal("bid"));
        entity.setBidSize(tick.getBigDecimal("bidSize"));
        entity.setAsk(tick.getBigDecimal("ask"));
        entity.setAskSize(tick.getBigDecimal("askSize"));
        entity.setLastSize(tick.getBigDecimal("lastSize"));
        return entity;
    }

    @Override
    public void send(String text) {
        super.send(text);
    }

    private void sendPong(Long time) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("pong", time);
        this.send(jsonObject.toString());
    }

    @Override
    public void onMessage(ByteBuffer bytes) {
        super.onMessage(bytes);
        try {
            String s = decodeByteBuf(bytes);
            onMessage(s);
        } catch (Exception e) {
            log.error("解压失败.", e);
        }
    }

    /**
     * 解压数据
     */
    private String decodeByteBuf(ByteBuffer buf) throws Exception {
        byte[] temp = buf.array();
        // gzip 解压
        temp = GZipUtils.decompress(temp);
        return new String(temp, StandardCharsets.UTF_8);
    }

    @Override
    public void onClose(int i, String s, boolean b) {
        System.out.println("onClose");
        log.error("被关闭了 {}", s);
    }

    @Override
    public void onError(Exception e) {
        System.out.println("onError");
        log.error("链接出错了", e);
    }

    private void watch() {
        if (!this.isOpen()) {
            if (this.getReadyState().equals(ReadyState.NOT_YET_CONNECTED)) {
                try {
                    log.info("火币 还没有链接，进行链接");
                    this.connect();
                } catch (IllegalStateException e) {
                    log.error("火币 socket链接出错了", e);
                }
            } else if (this.getReadyState().equals(ReadyState.CLOSING) || this.getReadyState()
                    .equals(ReadyState.CLOSED)) {
                log.warn("火币 被关闭了，发起重连");
                this.reconnect();
            }
        }
    }
}
