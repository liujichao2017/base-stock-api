package com.hm.stock.domain.stockdata.wsclient;

import cn.hutool.extra.spring.SpringUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.hm.stock.domain.market.entity.Market;
import com.hm.stock.domain.market.enums.StockDataSourceEnum;
import com.hm.stock.domain.market.mapper.MarketMapper;
import com.hm.stock.domain.stock.entity.Stock;
import com.hm.stock.domain.stock.mapper.StockMapper;
import com.hm.stock.domain.stockdata.StockApi;
import com.hm.stock.modules.common.LogicUtils;
import com.hm.stock.modules.utils.DateTimeUtil;
import com.hm.stock.modules.utils.HttpClient;
import lombok.extern.slf4j.Slf4j;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.enums.ReadyState;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

@Component
@Slf4j
@ConditionalOnProperty(prefix = "stock", name = "cna", havingValue = "true", matchIfMissing = false)
public class CnaStockWatchDog {
    private final List<CnaStockSocketClient> sinaStockSockets = new ArrayList<>();

    private final List<List<Stock>> stockList = new ArrayList<>();

    private final AtomicBoolean first = new AtomicBoolean(true);

    private CnaAigpStockStockClient aigpStockStockClient;

    private Market market;

    @Autowired
    private StockMapper stockMapper;
    @Autowired
    private MarketMapper marketMapper;

    @PostConstruct
    public void init() {
        this.aigpStockStockClient = new CnaAigpStockStockClient(marketMapper, stockMapper);
        queryStock();
        task();
    }

    private void queryStock() {
        stockList.clear();
        QueryWrapper<Market> ew = new QueryWrapper<>();
        ew.eq("data_source_mark", StockDataSourceEnum.CNA.getType());
        List<Market> markets = marketMapper.selectList(ew);
        if (LogicUtils.isEmpty(markets)) {
            return;
        }
        this.market = markets.get(0);
        QueryWrapper<Stock> qw = new QueryWrapper<>();
        qw.eq("market_id", market.getId());
        qw.eq("type", "1");
        List<Stock> stocks = stockMapper.selectList(qw);
        if (LogicUtils.isEmpty(stocks)) {
            return;
        }
        stockList.addAll(Lists.partition(stocks, 400));

    }

    @Scheduled(cron = "0 0/5 16 * * ?")
    public void rebuild() {
        for (CnaStockSocketClient sinaStockSocket : sinaStockSockets) {
            if (sinaStockSocket.isOpen()) {
                sinaStockSocket.close();
            }
        }
        sinaStockSockets.clear();
        queryStock();
        first.set(true);
        if (aigpStockStockClient.isOpen()) {
            aigpStockStockClient.isClosed();
        }
    }

//    @Async
//    @Scheduled(cron = "0/5 * * * * ?")
    public void task() {
        QueryWrapper<Market> ew = new QueryWrapper<>();
        ew.eq("data_source_mark", StockDataSourceEnum.CNA.getType());
        List<Market> markets = marketMapper.selectList(ew);
        if (LogicUtils.isEmpty(markets)) {
            return;
        }
        Market market = markets.get(0);
        if (!DateTimeUtil.isExpire(market.getTimeZone(), market.getTransAmBegin(), market.getTransAmEnd())
                && !DateTimeUtil.isExpire(market.getTimeZone(), market.getTransPmBegin(), market.getTransPmEnd())) {
            return;
        }
        List<List<Stock>> lists = stockList;
        if (LogicUtils.isEmpty(lists)) {
            return;
        }
        log.debug("我是看门狗，在检测心跳了.");
        try {
            if (first.get()) {
                for (List<Stock> list : lists) {
                    List<String> collect = list.stream().map(stock -> "s_" + stock.getGid())
                            .collect(Collectors.toList());
                    String join = LogicUtils.join(collect, ",");
                    HashMap<String, String> headers = new HashMap<>();
                    headers.put("Referer", "https://finance.sina.com");
                    sinaStockSockets.add(new CnaStockSocketClient("ws://hq.sinajs.cn/wskt?list=" + join, headers));
                }
            } else {
                for (int i = 0; i < sinaStockSockets.size(); i++) {
                    CnaStockSocketClient socket = sinaStockSockets.get(i);
                    watch(socket, "新浪-stock------" + (i + 1));
                }
            }

            first.set(false);
        } catch (Exception e) {
            log.error("看门狗出异常了。", e);
        }
    }

    @Async
    @Scheduled(cron = "0/5 * * * * ?")
    public void taskAigp() {
        QueryWrapper<Market> ew = new QueryWrapper<>();
        ew.eq("data_source_mark", StockDataSourceEnum.CNA.getType());
        List<Market> markets = marketMapper.selectList(ew);
        if (LogicUtils.isEmpty(markets)) {
            return;
        }
        Market market = markets.get(0);
        if (!DateTimeUtil.isExpire(market.getTimeZone(), market.getTransAmBegin(), market.getTransAmEnd())
                && !DateTimeUtil.isExpire(market.getTimeZone(), market.getTransPmBegin(), market.getTransPmEnd())) {
            return;
        }
        QueryWrapper<Stock> qw = new QueryWrapper<>();
        qw.eq("market_id", market.getId());
        List<Stock> stocks = stockMapper.selectList(qw);
        if (LogicUtils.isEmpty(stocks)) {
            return;
        }

        log.debug("我是看门狗，在检测心跳了.");
        try {
            watchAigp(aigpStockStockClient, "stock");
        } catch (Exception e) {
            log.error("看门狗出异常了。", e);
        }
    }

    private void watch(WebSocketClient client, String tag) {
        if (!client.isOpen()) {
            if (client.getReadyState().equals(ReadyState.NOT_YET_CONNECTED)) {
                try {
                    log.debug("还没有链接，进行链接");
                    client.connect();
                } catch (IllegalStateException e) {
                    log.error("socket链接出错了", e);
                }
            } else if (client.getReadyState().equals(ReadyState.CLOSING) || client.getReadyState()
                    .equals(ReadyState.CLOSED)) {
                log.debug("被关闭了，发起重连");
                client.reconnect();
            }
        } else {
            log.debug("{} 发送心跳包...", tag);
            client.send("ping");
        }
    }

    private void watchAigp(WebSocketClient client, String tag) {
        if (!client.isOpen()) {
            if (client.getReadyState().equals(ReadyState.NOT_YET_CONNECTED)) {
                try {
                    log.debug("还没有链接，进行链接");
                    client.connect();
                } catch (IllegalStateException e) {
                    log.error("socket链接出错了", e);
                }
            } else if (client.getReadyState().equals(ReadyState.CLOSING) || client.getReadyState().equals(ReadyState.CLOSED)) {
                log.warn("被关闭了，发起重连");
                client.reconnect();
            }
        } else {
            log.debug("{} 发送心跳包...", tag);
            client.sendPing();
            client.send("pong");
            client.send("ping");
        }
    }

    // 每天21点执行一次
    @Scheduled(cron = "0 1 9 * * MON-FRI")
    public void taskNew() throws InterruptedException {
        for (int i = 0; i < 5; i++) {
            try {
                String now = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                String content = HttpClient.sendGet("https://datacenter.eastmoney.com/securities/api/data/get?type=RPTA_DMSK_IPOMODULE&sty=ALL&p=1&ps=500&?v=09239783473363656",
                                                    "Referer","https://emdata.eastmoney.com/");
                log.info("新股 {}", content);
                JSONObject jsonObject = JSON.parseObject(content);
                if (!jsonObject.getBoolean("success")) {
                    log.error("拉取新股失败了  {}", jsonObject.getString("message"));
                    break;
                }
                JSONObject result = jsonObject.getJSONObject("result");
                JSONArray data = result.getJSONArray("data");
                JSONObject cell;
                for (Object datum : data) {
                    cell = (JSONObject) datum;

                    Integer isFromBj = cell.getInteger("IS_FROM_BJ");
                    if (isFromBj == null || isFromBj == 1) {
                        continue;
                    }
                    String codeFull = cell.getString("SECUCODE");
                    String[] split = codeFull.split("\\.");
                    String code = split[0];
                    String type = split[1].toLowerCase();
                    String gid = type.toLowerCase(Locale.getDefault()).concat(code);
                    String name = cell.getString("SECURITY_NAME");
                    String date = cell.getString("DATE");
                    String dataType = cell.getString("DATE_TYPE");
                    split = date.split("\\s+");

                    String publishDate = split[0];
                    if (!Objects.equals(publishDate, now)) {
                        log.warn("发布日期不是当日 跳过这只股票. {}", cell);
                        continue;
                    }
                    if (Objects.equals(dataType, "上市")) {
                        saveNewStock(cell, code, type, gid, name);
                    } else if (Objects.equals(dataType, "申购")) {
//                        if (!newPublish) {
//                            break;
//                        }
//                        saveNewList(cell, code, type, gid, name);
                    }

                }
                break;
            } catch (Exception e) {
                log.error("同步新股失败", e);
                TimeUnit.SECONDS.sleep(1);
            }
        }

    }

    private void saveNewList(JSONObject cell, String code, String type, String gid, String name) {
        //全部调整为不显示
//        Long count = newlistDao.selectCount(new LambdaQueryWrapper<Newlist>().eq(Newlist::getCode, code));
//        if (count > 0) {
//            log.warn("新股存在了===> {}", name);
//            return;
//        }
//        Newlist newlist = new Newlist();
//        newlist.setCode(code);
//        newlist.setNames(name);
//        newlist.setZhangshu(1);
//        //0 隐藏 1显示
//        newlist.setZt(1);
////        新股归属类型：0 不显示，1 创，2  科，3 北
//        newlist.setIpoType(0);
//        newlist.setDataType(1);
//        newlist.setPrice(cell.get("ISSUE_PRICE").toString());
//        newlist.setPurchaseTime(
//                LocalDateTime.parse(cell.getString("DATE"), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
//        newlistDao.insert(newlist);
//
//        //新股抢筹
//        newlist.setDataType(2);
//        newlistDao.insert(newlist);

    }


    private void saveNewStock(JSONObject cell, String code, String type, String gid, String name) {
        Long count = stockMapper.selectCount(new LambdaQueryWrapper<Stock>().eq(Stock::getGid, gid));
        if (count > 0) {
            log.warn("股票存在了===> {}", name);
            return;
        }
        Stock entity = new Stock();
        entity.setCode(code);
        entity.setGid(gid);
        entity.setName(name);
        entity.setExchanges(type);
        entity.setType("1");
        entity.setMarketId(this.market.getId());

        StockApi instance = StockApi.getInstance(this.market);
        Stock stock = instance.getStock(this.market, gid);

        entity.setLast(stock.getLast());
        entity.setChg(stock.getChg());
        entity.setChgPct(stock.getChgPct());
        entity.setHigh(stock.getHigh());
        entity.setLast(stock.getLast());
        entity.setOpen(stock.getOpen());
        entity.setClose(stock.getClose());
        entity.setVolume(stock.getVolume());
        entity.setAmounts(stock.getAmounts());

        stockMapper.insert(entity);
        rebuild();
    }
}
