package com.hm.stock.domain.stockdata.wsclient;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.hm.stock.domain.market.entity.Market;
import com.hm.stock.domain.market.enums.StockDataSourceEnum;
import com.hm.stock.domain.market.mapper.MarketMapper;
import com.hm.stock.domain.stock.entity.Stock;
import com.hm.stock.domain.stock.mapper.StockMapper;
import com.hm.stock.modules.common.LogicUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.jetbrains.annotations.Nullable;
import org.springframework.util.ObjectUtils;

import javax.annotation.PreDestroy;
import java.math.BigDecimal;
import java.net.URI;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Slf4j
public class CnaAigpStockStockClient extends WebSocketClient {
    private final Map<String, Stock> stockMap = new ConcurrentHashMap<>();

    private final StockMapper stockMapper;
    private final MarketMapper marketMapper;

    public CnaAigpStockStockClient(MarketMapper marketMapper, StockMapper stockMapper) {
        super(URI.create("wss://im.aigupiao.com/ws/quote"));
        setConnectionLostTimeout(30);
        this.stockMapper = stockMapper;
        this.marketMapper = marketMapper;
        List<Stock> stocks = queryStock(marketMapper, stockMapper);
        if (stocks != null && !stocks.isEmpty()){
            this.stockMap.clear();
            this.stockMap.putAll(stocks.stream().collect(Collectors.toMap(Stock::getGid, s -> s)));
        }
    }

    public CnaAigpStockStockClient(URI serverUri, MarketMapper marketMapper, StockMapper stockMapper) {
        super(serverUri);
        this.stockMapper = stockMapper;
        this.marketMapper = marketMapper;
        List<Stock> stocks = queryStock(marketMapper, stockMapper);
        if (stocks != null && !stocks.isEmpty()){
            this.stockMap.clear();
            this.stockMap.putAll(stocks.stream().collect(Collectors.toMap(Stock::getGid, s -> s)));
        }

    }


    @Override
    public void onOpen(ServerHandshake serverHandshake) {
        log.debug("打开了链接...");

        JSONObject obj = new JSONObject(2);
        obj.put("kind", "subscribe");
        obj.put("data", StringUtils.join(stockMap.keySet(), ","));
        send(obj.toJSONString());
    }

    @Override
    public void onMessage(String s) {
        log.debug("收到消息 {} ", s);
        try {

            if ("pong".equalsIgnoreCase(s)) {
                log.info("收到服务回复心跳了");
                return;
            }

            JSONObject jsonObject = JSON.parseObject(s);
            JSONObject data = jsonObject.getJSONObject("data");
            data.forEach((s1, o) -> {
                try {
                    List<String> list = Arrays.stream(o.toString().split(",")).collect(Collectors.toList());
                    if (ObjectUtils.isEmpty(list)) {
                        return;
                    }

                    Stock stock = stockMap.get(s1);
                    if (stock == null) {
                        return;
                    }
                    // 1 最新价格 2 昨收 3 涨跌幅 4总涨跌  5 今开 6 最高 7 最低 8 涨停 9 10 成交量 11 成交额
                    Stock entity = new Stock();
                    entity.setId(stock.getId());
                    entity.setLast(new BigDecimal(list.get(0)));
                    entity.setClose(new BigDecimal(list.get(1)));
                    entity.setChg(new BigDecimal(list.get(2)));
                    entity.setChgPct(new BigDecimal(list.get(3)));
                    entity.setOpen(new BigDecimal(list.get(4)));
                    entity.setHigh(new BigDecimal(list.get(5)));
                    entity.setLow(new BigDecimal(list.get(6)));
                    // 涨停
//                    entity.setZt(new BigDecimal(list.get(7)));
                    entity.setVolume(new BigDecimal(list.get(9)));
                    entity.setAmounts(new BigDecimal(list.get(10)));


                    stockMapper.updateById(entity);
                } catch (Exception e) {
                    log.error("这个票出问题了" + s1 + "\n  Exception", e);
                }
            });
        } catch (Exception e) {
            log.error("收到消息了  {} 但是出问题了=> {}", s, e);
        }

    }

    @Override
    public void onMessage(ByteBuffer bytes) {
        super.onMessage(bytes);
        CharsetDecoder decoder = StandardCharsets.UTF_8.newDecoder();
        try {
            CharBuffer decode = decoder.decode(bytes.asReadOnlyBuffer());
            this.onMessage(decode.toString());
        } catch (Exception e) {
            log.error("收到消息出异常了", e);
        }


    }

    @PreDestroy
    public void disconnect() {
        super.close();
    }

    @Override
    public void onClose(int code, String s, boolean b) {
        log.warn("链接关闭了 url=>{} code=>{} err=>{} b=>{}", getURI(), code, s, b);

    }

    @Override
    public void onError(Exception e) {
        log.error("socker 发生错误了 ", e);
    }

    private static List<Stock> queryStock(MarketMapper marketMapper, StockMapper stockMapper) {
        QueryWrapper<Market> ew = new QueryWrapper<>();
        ew.eq("data_source_mark", StockDataSourceEnum.CNA.getType());
        List<Market> markets = marketMapper.selectList(ew);
        if (LogicUtils.isEmpty(markets)) {
            return null;
        }
        Market market = markets.get(0);
        QueryWrapper<Stock> qw = new QueryWrapper<>();
        qw.eq("market_id", market.getId());
        List<Stock> stocks = stockMapper.selectList(qw);
        if (LogicUtils.isEmpty(stocks)) {
            return null;
        }
        return stocks;
    }
}

