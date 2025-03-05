package com.hm.stock.domain.stockdata.reset;

import cn.hutool.core.collection.ListUtil;
import cn.hutool.extra.spring.SpringUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.hm.stock.domain.market.entity.Market;
import com.hm.stock.domain.stock.entity.Stock;
import com.hm.stock.domain.stock.mapper.StockMapper;
import com.hm.stock.domain.stockdata.StockApi;
import com.hm.stock.domain.stockdata.reset.vo.CnAConfig;
import com.hm.stock.domain.stockdata.reset.vo.Kline;
import com.hm.stock.modules.common.LogicUtils;
import com.hm.stock.modules.utils.DateTimeUtil;
import com.hm.stock.modules.utils.HttpClient;
import com.hm.stock.modules.utils.JsonUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class CnAStockApi implements StockApi {
    private final ThreadLocal<CnAConfig> threadLocal = new ThreadLocal<>();

    @Autowired
    private StockMapper stockMapper;

    @Override
    public void init(Market market) {
        threadLocal.set(JsonUtil.toObj(market.getDataSourceJson(), CnAConfig.class));
    }

    @Override
    public List<Stock> getList(Market market) {
        CnAConfig cnAConfig = threadLocal.get();
        String res = HttpClient.sendGet("https://ig507.com/data/base/gplist?licence=" + cnAConfig.getLicence());
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
        threadLocal.remove();
        return stocks;
    }

    @Override
    public List<Stock> getIndex(Market market) {
        List<Stock> indexList = new ArrayList<>();

        Stock stock = new Stock();
        stock.setName("上证指数");
        stock.setCode("000001");
        stock.setExchanges("sh");
        stock.setGid("sh000001");
        stock.setMarketId(market.getId());
        stock.setType("2");
        indexList.add(stock);

        stock = new Stock();
        stock.setName("深证成指");
        stock.setCode("399001");
        stock.setExchanges("sz");
        stock.setGid("sz399001");
        stock.setMarketId(market.getId());
        stock.setType("2");
        indexList.add(stock);

        stock = new Stock();
        stock.setName("创业板指");
        stock.setCode("399006");
        stock.setExchanges("sz");
        stock.setGid("sz399006");
        stock.setMarketId(market.getId());
        stock.setType("2");
        indexList.add(stock);

        stock = new Stock();
        stock.setName("深证综指");
        stock.setCode("399106");
        stock.setExchanges("sz");
        stock.setGid("sz399106");
        stock.setMarketId(market.getId());
        stock.setType("2");
        indexList.add(stock);

        return indexList;
    }

    @Override
    public Stock getStock(Market market, String gid) {
        Stock stock = new Stock();
        String s = HttpClient.sendGet("https://www.aigupiao.com/Quote/get_stock_handicap?code=" + gid);
        JSONObject quote = JSON.parseObject(s).getJSONObject("data").getJSONObject("quote");
        stock.setGid(gid);
        stock.setChg(BigDecimal.valueOf(
                Double.parseDouble(Optional.ofNullable(quote.getString("pc")).orElse(quote.getString("chg")))));
        stock.setChgPct(BigDecimal.valueOf(
                Double.parseDouble(Optional.ofNullable(quote.getString("pcp")).orElse(quote.getString("chg_pct")))));
        stock.setLast(new BigDecimal(Optional.ofNullable(quote.getString("last")).map(p -> "--".equals(p) ? "0" : p)
                                             .orElse(quote.getString("Last"))));
        stock.setHigh(new BigDecimal(Optional.ofNullable(quote.getString("high")).map(p -> "--".equals(p) ? "0" : p)
                                             .orElse(quote.getString("High"))));
        stock.setLow(new BigDecimal(Optional.ofNullable(quote.getString("low")).map(p -> "--".equals(p) ? "0" : p)
                                            .orElse(quote.getString("Low"))));
        stock.setOpen(new BigDecimal(Optional.ofNullable(quote.getString("open")).map(p -> "--".equals(p) ? "0" : p)
                                             .orElse(quote.getString("Low"))));

        stock.setClose(new BigDecimal(
                Optional.ofNullable(quote.getString("hst_close")).map(p -> "--".equals(p) ? "0" : p)
                        .orElse(quote.getString("hst_close"))));
        stock.setVolume(new BigDecimal(Optional.ofNullable(quote.getString("volume")).map(p -> "--".equals(p) ? "0" : p)
                                               .orElse(quote.getString("setVolume"))));
        stock.setAmounts(new BigDecimal(
                Optional.ofNullable(quote.getString("amount")).map(p -> "--".equals(p) ? "0" : p)
                        .orElse(quote.getString("amount"))).multiply(BigDecimal.valueOf(10000)));


        stock.setAmounts(new BigDecimal(Double.parseDouble(quote.getString("amount")) * 10000));
        return stock;
    }

    @Override
    public Stock getStockByWs(Market market, Stock stock) {
        if ("2".equals(stock.getType())) {
            return getStock(market, stock.getGid());
        }
        return stock;
    }

    @Override
    public List<Stock> getStocks(Market market, List<String> gids) {
        return gids.stream().map(gid -> getStock(market, gid)).collect(Collectors.toList());
    }

    @Override
    public String getKline(Market market, String gid, String time) {
        CnAConfig cnAConfig = threadLocal.get();
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
            if ("1".equals(time)) {
                String s = getIndexData("5", stock, cnAConfig);
                List<Kline> time5List = parseKline(market, s);
                return JsonUtil.toStr(convertTime1(time5List));
            }
            String s = getIndexData(time, stock, cnAConfig);
            List<Kline> obj = parseKline(market, s);
            if (LogicUtils.isNotEmpty(obj) && LogicUtils.isEquals("Day", time)) {
                try {
                    List<Kline> list = parseKline(market, getIndexData("5", stock, cnAConfig));
                    Kline kline = list.get(list.size() - 1);
                    String t = new SimpleDateFormat("yyyy-MM-dd").format(new Date(kline.getT() * 1000));
                    String d = new SimpleDateFormat("yyyy-MM-dd").format(
                            new Date(obj.get(obj.size() - 1).getT() * 1000));

                    if (t.compareTo(d) > 0) {
                        kline.setO(stock.getOpen());
                        kline.setC(stock.getLast());
                        kline.setH(stock.getHigh());
                        kline.setL(stock.getLast());
                        obj.add(kline);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            if (LogicUtils.isEmpty(obj) && LogicUtils.isAny(time, "D", "W", "M")) {
                try {
                    List<Kline> list = parseKline(market, getIndexData("5", stock, cnAConfig));
                    Kline kline = list.get(list.size() - 1);
                    kline.setO(stock.getOpen());
                    kline.setC(stock.getLast());
                    kline.setH(stock.getHigh());
                    kline.setL(stock.getLast());
                    obj.add(kline);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return JsonUtil.toStr(obj);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            threadLocal.remove();
        }
        return "[]";
    }

    private static String getIndexData(String time, Stock stock, CnAConfig cnAConfig) {
        if ("1".equals(stock.getType())) {
            return HttpClient.sendGet(
                    String.format("https://ighk.stocskapis.com/data/time/history/trade/%s/%s?licence=%s",
                                  stock.getCode(), time, cnAConfig.getLicence()));
        }
        return HttpClient.sendGet(
                String.format("https://ighk.stocskapis.com/data/time/history/trade/%s/%s?licence=%s",
                              stock.getGid(), time, cnAConfig.getLicence()));
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
            e.printStackTrace();
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

    public static void main(String[] args) {
        String url = "https://ighk.stocskapis.com/data/time/history/trade/%s/%s?licence=8ECF09E3-9D5F-06FF-FE5C-B87909A5411D";
        url = String.format(url, "399106", "Day");
        System.out.println(url);
        String res = HttpClient.sendGet(url);
        System.out.println(res);
        if ("404无资源\n".equals(res)) {
            url = "https://ighk.stocskapis.com/data/time/history/trade/%s/%s?licence=8ECF09E3-9D5F-06FF-FE5C-B87909A5411D";
            url = String.format(url, "sz399006", "Day");
            System.out.println(url);
            res = HttpClient.sendGet(url);
        }
        System.out.println(res);
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
