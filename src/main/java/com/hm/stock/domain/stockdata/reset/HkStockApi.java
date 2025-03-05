package com.hm.stock.domain.stockdata.reset;

import cn.hutool.core.collection.ListUtil;
import cn.hutool.extra.spring.SpringUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.google.common.collect.Lists;
import com.hm.stock.domain.market.entity.Market;
import com.hm.stock.domain.market.enums.StockDataSourceEnum;
import com.hm.stock.domain.market.mapper.MarketMapper;
import com.hm.stock.domain.stock.entity.Stock;
import com.hm.stock.domain.stock.entity.StockHkBearbull;
import com.hm.stock.domain.stock.mapper.StockHkBearbullMapper;
import com.hm.stock.domain.stock.mapper.StockMapper;
import com.hm.stock.domain.stock.service.StockService;
import com.hm.stock.domain.stockdata.StockApi;
import com.hm.stock.domain.stockdata.reset.vo.DcResponse;
import com.hm.stock.domain.stockdata.reset.vo.HkConfig;
import com.hm.stock.domain.stockdata.reset.vo.Kline;
import com.hm.stock.modules.common.LogicUtils;
import com.hm.stock.modules.enums.YNEnum;
import com.hm.stock.modules.execptions.CommonResultCode;
import com.hm.stock.modules.execptions.InternalException;
import com.hm.stock.modules.utils.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

@Component
@Slf4j
public class HkStockApi implements StockApi {
    @Autowired
    private StockMapper stockMapper;

    @Autowired
    private MarketMapper marketMapper;

    private final ThreadLocal<HkConfig> threadLocal = new ThreadLocal<>();

    // 启动第一次同步数据
    private final AtomicBoolean flag = new AtomicBoolean(true);

    @PostConstruct
    public void sync() {
        if (Boolean.FALSE.toString().equals(SpringUtil.getProperty("stock.hk"))){
            return;
        }
        new Thread(() -> {
            while (true) {
                try {
                    QueryWrapper<Market> ew = new QueryWrapper<>();
                    ew.eq("data_source_mark", StockDataSourceEnum.HK.getType());
                    List<Market> markets = marketMapper.selectList(ew);
                    if (LogicUtils.isEmpty(markets)) {
                        continue;
                    }
                    Market market = markets.get(0);
                    if (flag.get()
                            || DateTimeUtil.isExpire(market.getTimeZone(), market.getTransAmBegin(), market.getTransAmEnd())
                            || DateTimeUtil.isExpire(market.getTimeZone(), market.getTransPmBegin(), market.getTransPmEnd())) {
                        QueryWrapper<Stock> wq = new QueryWrapper<>();
                        wq.eq("market_id", market.getId());
                        wq.eq("type", "1");
                        List<Stock> stocks = stockMapper.selectList(wq);
                        List<List<Stock>> partition = Lists.partition(stocks, 400);
                        for (List<Stock> stockList : partition) {
                            ExecuteUtil.STOCK_HK.execute(() -> syncStock(stockList));
                        }
                        log.info("同步相关股票: {}", stocks.size());
                        flag.set(false);
                    }
                    TimeUnit.MINUTES.sleep(5);
                } catch (Exception e) {
                    log.error("同步股票失败: {}", e);
                }
            }
        }).start();
    }

    private void syncStock(List<Stock> list) {
        List<Stock> stocks = list.stream().map(this::getStock).collect(Collectors.toList());
        stocks.forEach(stockMapper::updateById);
    }


    @Override
    public void init(Market market) {
        threadLocal.set(JsonUtil.toObj(market.getDataSourceJson(), HkConfig.class));
    }

    @Override
    public List<Stock> getList(Market market) {

        String res = HttpClient.sendGet("http://47.57.188.211/data/base/gplisthk");
        if (LogicUtils.isBlank(res)) {
            return Collections.emptyList();
        }
        JSONArray jsonArray = JSONObject.parseArray(res);
        if (LogicUtils.isEmpty(jsonArray)) {
            return Collections.emptyList();
        }
        List<Stock> stocks = new ArrayList<>();
        for (int i = 0; i < jsonArray.size(); i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            String stockType = jsonObject.get("jys").toString();
            String stockName = jsonObject.get("mc").toString();
            String stockCode = jsonObject.get("dm").toString();
            Stock stock = new Stock();
            stock.setName(stockName);
            stock.setCode(stockCode);
            stock.setExchanges(stockType.toUpperCase(Locale.ENGLISH));
            stock.setGid(stockType + stockCode);
            stock.setMarketId(market.getId());
            stock.setType("1");
            stocks.add(stock);
        }
        HkConfig hkConfig = threadLocal.get();
        if (YNEnum.yes(hkConfig.getBearbull())) {
            ExecuteUtil.STOCK.execute(() -> {
                syncBearbull();
            });
        }
        return stocks;
    }

    private void syncBearbull() {
        String url = "http://206.238.199.99:9305/api/reptile/aastocks/page/";
        String res = HttpClient.sendGet(url + 1);
        JSONObject obj = JSONObject.parseObject(res);
        JSONObject data = obj.getJSONObject("data");
        Long total = data.getLong("total");

        JSONArray list = data.getJSONArray("list");
        List<StockHkBearbull> javaList = list.toJavaList(StockHkBearbull.class);
        for (StockHkBearbull stockHkBearbull : javaList) {
            StockHkBearbullMapper mapper = SpringUtil.getBean(StockHkBearbullMapper.class);
            try {
                mapper.insert(stockHkBearbull);
            } catch (Exception e) {
                mapper.updateById(stockHkBearbull);
            }
        }
        int size = (int) ((total / 1000) + (total % 1000 == 0 ? 0 : 1));
        for (int i = 2; i <= size; i++) {
            res = HttpClient.sendGet(url + i);
            obj = JSONObject.parseObject(res);
            data = obj.getJSONObject("data");
            list = data.getJSONArray("list");
            javaList = list.toJavaList(StockHkBearbull.class);
            for (StockHkBearbull stockHkBearbull : javaList) {
                StockHkBearbullMapper mapper = SpringUtil.getBean(StockHkBearbullMapper.class);
                try {
                    mapper.insert(stockHkBearbull);
                } catch (Exception e) {
                    mapper.updateById(stockHkBearbull);
                }
            }
        }
    }

    @Override
    public List<Stock> getIndex(Market market) {
        List<Stock> indexList = new ArrayList<>();

        Stock stock = new Stock();
        stock.setName("恒生指数");
        stock.setCode("HSI");
        stock.setExchanges("hk");
        stock.setGid("hkHSI");
        stock.setMarketId(market.getId());
        stock.setType("2");
        indexList.add(stock);

        stock = new Stock();
        stock.setName("国企指数");
        stock.setCode("HSCEI");
        stock.setExchanges("hk");
        stock.setGid("hkHSCEI");
        stock.setMarketId(market.getId());
        stock.setType("2");
        indexList.add(stock);

        stock = new Stock();
        stock.setName("恒生科技指数");
        stock.setCode("HSTECH");
        stock.setExchanges("hk");
        stock.setGid("hkHSTECH");
        stock.setMarketId(market.getId());
        stock.setType("2");
        indexList.add(stock);


        return indexList;
    }

    @Override
    public Stock getStock(Market market, String gid) {
        StockService stockService = BeanUtil.getBean(StockService.class);
        Stock stock = stockService.getSingeStock(gid);
        if ("1".equals(stock.getType())) {
            return getStock(stock);
        }
        if ("2".equals(stock.getType())) {
            return getStockByIndex(stock);
        }
        throw new InternalException(CommonResultCode.INTERNAL_ERROR);
    }

    private Stock getStock(Stock stock) {
        String s = HttpClient.sendGet("http://47.57.188.211/data/time/hk/real/" + stock.getCode());
        JSONObject data = JSON.parseObject(s);
        stock.setChg(data.getBigDecimal("ud"));
        stock.setChgPct(data.getBigDecimal("pc"));
        stock.setLast(data.getBigDecimal("p"));
        stock.setHigh(data.getBigDecimal("h"));
        stock.setLow(data.getBigDecimal("l"));
        stock.setOpen(data.getBigDecimal("o"));
        stock.setClose(data.getBigDecimal("yc"));
        String cjl;
        try {
            if(null == data.get("v")){
                cjl = "0.0";
            }else {
                cjl = data.get("v").toString();
            }
        }catch (Exception e){
            cjl = "0.0";
        }
        stock.setVolume(new BigDecimal(cjl));

        String cje;
        try {
            if(null == data.getString("cje")){
                cje = "0.0";
            }else {
                cje = data.getString("cje");
            }
        }catch (Exception e){
            cje = "0.0";
        }

        stock.setAmounts(new BigDecimal(cje));
        return stock;
    }

    private Stock getStockByIndex(Stock stock) {
        String indexCode = stock.getCode();
        String indexGid = "";
        if (indexCode.equals("HSI")) {
            indexGid = "100.HSI";
        } else if (indexCode.equals("HSCEI")) {
            indexGid = "100.HSCEI";
        } else if (indexCode.equals("HSTECH")) {
            indexGid = "124.HSTECH";
        } else if (indexCode.contains("000001")) {
            indexGid = "1.000001";
        }
        DcResponse dcResponse = null;
        try {
            String s = HttpClient.sendGet(String.format(
                                                  "http://push2delay.eastmoney.com/api/qt/stock/get?invt=2&fltt=1&cb=json&fields=" +
                                                          "f58,f107,f57,f43,f59,f169,f170,f152,f46,f60,f44,f45,f171,f47,f48,f86,f292&secid=%s&_=%d",
                                                  indexGid, System.currentTimeMillis()),
                                          "Referer",
                                          String.format("http://quote.eastmoney.com/gb/zs%s.html", stock.getCode()));
            s = s.replace("json(", "").replace(");", "");
            dcResponse = JSON.parseObject(s, DcResponse.class);
            if (dcResponse.getRc() != 0) {
                int i = 0;
                do {
                    s = HttpClient.sendGet(String.format(
                                                   "http://push2delay.eastmoney.com/api/qt/stock/get?invt=2&fltt=1&cb=json&fields=" +
                                                           "f58,f107,f57,f43,f59,f169,f170,f152,f46,f60,f44,f45,f171,f47,f48,f86,f292&secid=%s&_=%d",
                                                   indexGid, System.currentTimeMillis()),
                                           "Referer",
                                           String.format("http://quote.eastmoney.com/gb/zs%s.html", stock.getCode()));
                    s = s.replace("json(", "").replace(");", "");
                    dcResponse = JSON.parseObject(s, DcResponse.class);
                    if (dcResponse.getRc() == 0) {
                        break;
                    }
                    i++;
                } while (i < 3);
            }
        } catch (Exception e) {
            dcResponse = new DcResponse();
            dcResponse.setRc(1);
            log.error("查询港股指数失败: {}-{}-{}:{} ", stock.getName(), stock.getCode(), indexGid, e.getMessage());
            log.debug("查询港股指数失败: ", e);
        }

        if (dcResponse.getRc() != 0) {
            return stock;
        }
        DcResponse.DataBean data = dcResponse.getData();
        stock.setChg(BigDecimal.valueOf(data.getF169()).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP));
        stock.setChgPct(BigDecimal.valueOf(data.getF170()).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP));
        stock.setLast(BigDecimal.valueOf(data.getF43()).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP));
        stock.setHigh(BigDecimal.valueOf(data.getF44()).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP));
        stock.setLow(BigDecimal.valueOf(data.getF45()).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP));
        stock.setOpen(BigDecimal.valueOf(data.getF46()).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP));
        stock.setClose(BigDecimal.valueOf(data.getF60()).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP));
        return stock;
    }

    @Override
    public Stock getStockByWs(Market market, Stock stock) {
        return getStock(market, stock.getGid());
    }

    @Override
    public List<Stock> getStocks(Market market, List<String> gids) {
        return gids.stream().map(gid -> getStock(market, gid)).collect(Collectors.toList());
    }

    @Override
    public String getKline(Market market, String gid, String time) {
        try {
            switch (time) {
                case "D":
                    time = "Day";
                    break;
                case "W":
                    time = "Week";
                    break;
                case "M":
                    time = "Month";
                    break;
            }
            QueryWrapper<Stock> ew = new QueryWrapper<>();
            ew.eq("gid", gid);
            ew.last("limit 1");
            Stock stock = stockMapper.selectOne(ew);
            if (LogicUtils.isNull(stock)) {
                return "[]";
            }
            if ("1".equals(stock.getType())) {
                return getStockByKline(market, time, stock);
            }
            if ("2".equals(stock.getType())) {
                return getIndexByKline(market, time, stock);
            }
            return "[]";
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "[]";
    }

    private static String getIndexByKline(Market market, String time, Stock stock) {
        int type = 8;
        switch (time) {
            case "1":
                type = 1;
                break;
            case "5":
                type = 2;
                break;
            case "10":
                type = 2;
                break;
            case "15":
                type = 3;
                break;
            case "30":
                type = 4;
                break;
            case "60":
                type = 5;
                break;
            case "Day":
                type = 8;
                break;
            case "Week":
                type = 9;
                break;
            case "Month":
                type = 10;
                break;
        }

        String code = stock.getCode();
        String area = "HK";
        if (code.contains("000001")) {
            area = "SH";
        }

        if (type == 6) {
            type = 10;
        }

        String res = getIndexKline(area, code, type);


        return buildIndexKline(res);
    }

    private static String getIndexKline(String area, String code, int type) {

        try {
            String query = String.format("{\n" +
                                                 "  \"trace\": \"3baaa938-f92c-4a74-a228-fd49d5e2f8bc-1678419657806\",\n" +
                                                 "  \"data\": {\n" +
                                                 "    \"code\": \"%s." + area + "\",\n" +
                                                 "    \"kline_type\": %d,\n" +
                                                 "    \"kline_timestamp_end\": 0,\n" +
                                                 "    \"query_kline_num\": 1000,\n" +
                                                 "    \"adjust_type\": 0\n" +
                                                 "  }\n" +
                                                 "}", code, type);
            return HttpClient.sendGet("http://quote.tradeswitcher" +
                                              ".com/quote-stock-b-api/kline?token=6887d8af8276cd66d3d8a615a3087643-c-app&query=" + URLEncoder.encode(
                    query, StandardCharsets.UTF_8.toString()));
        } catch (Exception e) {
            log.error("请求港股K线数据失败: ", e);
            return "{}";
        }
    }

    private static String buildIndexKline(String res) {
        JSONObject result = JSONObject.parseObject(res);
        JSONObject data = result.getJSONObject("data");

        JSONArray klineList = data.getJSONArray("kline_list");
        List<Kline> list = new ArrayList<>();
        for (int i = 0; i < klineList.size(); i++) {
            JSONObject item = klineList.getJSONObject(i);
            Kline kline = new Kline();
            kline.setT(item.getLong("timestamp"));
            kline.setC(item.getBigDecimal("close_price"));
            kline.setO(item.getBigDecimal("open_price"));
            kline.setH(item.getBigDecimal("high_price"));
            kline.setL(item.getBigDecimal("low_price"));
            kline.setV(item.getBigDecimal("volume"));
            kline.setVo(item.getBigDecimal("turnover"));
            list.add(kline);
        }
        return JsonUtil.toStr(list);
    }

    private static String getStockByKline(Market market, String time, Stock stock) {
        if ("1".equals(time)) {
            String s = HttpClient.sendGet(
                    String.format("http://47.57.188.211/data/time/hk/history/trade/%s/%s", stock.getCode(), "5"));
            return JsonUtil.toStr(convertTime1(parseKline(market, s)));
        }
        String s = HttpClient.sendGet(
                String.format("http://47.57.188.211/data/time/hk/history/trade/%s/%s", stock.getCode(), time));
        return JsonUtil.toStr(parseKline(market, s));
    }

    private static List<Kline> parseKline(Market market, String res) {
        List<Kline> list = new ArrayList<>();
        JSONArray arr = JSONObject.parseArray(res);
        for (int i = 0; i < arr.size(); i++) {
            JSONObject obj = arr.getJSONObject(i);
            Kline kline = new Kline();
            kline.setT(parseTime(obj.getString("d"), DateTimeUtil.getTimestamp(market.getTimeZone())));
            kline.setC(getBigDecimalVal(obj, "c"));
            kline.setO(getBigDecimalVal(obj, "o"));
            kline.setH(getBigDecimalVal(obj, "h"));
            kline.setL(getBigDecimalVal(obj, "l"));
            kline.setV(getBigDecimalVal(obj, "e"));
            kline.setVo(getBigDecimalVal(obj, "v"));
            list.add(kline);
        }
        return list;
    }

    public static Long parseTime(String time, int timestamp) {
        try {
            LocalDateTime dateTime = DateTimeUtil.getLocalDateTime(time);
            return dateTime.toInstant(DateTimeUtil.getZoneOffset(timestamp)).toEpochMilli() / 1000;
        } catch (Exception e) {
            log.error("时间格式化异常: {}",time);
            return System.currentTimeMillis() / 1000;
        }
    }


    private static BigDecimal getBigDecimalVal(JSONObject obj, String key) {
        try {
            BigDecimal val = obj.getBigDecimal(key);
            if (val == null) {
                return BigDecimal.ZERO;
            }
            return val;
        } catch (Exception e) {
            return BigDecimal.ZERO;
        }
    }

    public static void main(String[] args) throws Exception {
        String res = getIndexKline("HK", "HSCEI", 1);
        String s = buildIndexKline(res);
        System.out.println(s);
    }


    public static List<Kline> convertTime1(List<Kline> time5List) {
        List<Kline> sub = ListUtil.sub(time5List, time5List.size() - (time5List.size() / 5), time5List.size());
        List<Kline> time1List = new ArrayList<>();
        for (int i = 1; i < sub.size(); i++) {
            time1List.addAll(genTime1(sub.get(i - 1), sub.get(i)));
        }
        time1List.add(time5List.get(time5List.size() - 1));
        return time1List;
    }

    private static List<Kline> genTime1(Kline kline1, Kline kline2) {
        int count = 5;
        List<Kline> time = new ArrayList<>();
        time.add(kline1);
        BigDecimal diff = kline2.getC().subtract(kline1.getC());
        diff = diff.divide(BigDecimal.valueOf(count), 2, 4);
        BigDecimal c = kline1.getC();
        Long t = kline1.getT();
        for (int i = 1; i < 5; i++) {
            t = t + 60;
            c = c.add(diff);
            Kline kline = new Kline();
            kline.setT(t);
            kline.setC(c);
            kline.setO(kline1.getO());
            kline.setH(kline1.getH().compareTo(c) < 0 ? c : kline1.getH());
            kline.setL(kline1.getL().compareTo(c) > 0 ? c : kline1.getL());
            kline.setV(kline1.getV());
            kline.setVo(kline1.getVo());
            time.add(kline);
        }

        return time;
    }
}
