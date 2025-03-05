package com.hm.stock.domain.stockdata.reset;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.hm.stock.domain.market.entity.Market;
import com.hm.stock.domain.stock.entity.Stock;
import com.hm.stock.domain.stockdata.StockApi;
import com.hm.stock.domain.stockdata.reset.vo.JsConfig;
import com.hm.stock.modules.common.LogicUtils;
import com.hm.stock.modules.utils.HttpClient;
import com.hm.stock.modules.utils.JsonUtil;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 匠山数据
 */
@Slf4j
@Component
public class JsStockApi implements StockApi {

    private final ThreadLocal<JsConfig> threadLocal = new ThreadLocal<>();


    @Override
    public void init(Market market) {
        threadLocal.set(JsonUtil.toObj(market.getDataSourceJson(), JsConfig.class));
    }

    @Override
    public List<Stock> getList(Market market) {
        JsConfig jsConfig = threadLocal.get();
        List<Stock> stocks = new ArrayList<>();
        try {
            for (int i = 0; i < 100; i++) {
                String res = HttpClient.sendGet(jsConfig.getDomain() + "/list" + String.format(
                        "?country_id=" + jsConfig.getCountryId() + "&size=1000&page=%d&key=%s", i, jsConfig.getKey()));
                if (LogicUtils.isBlank(res)) {
                    break;
                }
                JSONObject obj = JSONObject.parseObject(res);
                JSONArray arr = obj.getJSONArray("data");
                if (LogicUtils.isEmpty(arr)) {
                    break;
                }
                for (int i1 = 0; i1 < arr.size(); i1++) {
                    JSONObject result = arr.getJSONObject(i1);
                    Stock stock = buildStock(result);
                    stock.setType("1");
                    stock.setMarketId(market.getId());
                    stocks.add(stock);
                }
            }
        } catch (Exception e) {
            log.error("请求匠山数据错误: config: {}", jsConfig, e);
        }
        threadLocal.remove();
        return stocks;
    }

    @Override
    public List<Stock> getIndex(Market market) {
        JsConfig jsConfig = threadLocal.get();
        List<Stock> stocks = new ArrayList<>();
        try {
            String res = HttpClient.sendGet(jsConfig.getDomain() + "/indices" + String.format(
                    "?country_id=" + jsConfig.getCountryId() + "&key=%s", jsConfig.getKey()));
            if (LogicUtils.isBlank(res)) {
                return stocks;
            }
            JSONArray arr = JSONObject.parseArray(res);
            if (LogicUtils.isEmpty(arr)) {
                return stocks;
            }
            for (int i1 = 0; i1 < arr.size(); i1++) {
                JSONObject result = arr.getJSONObject(i1);
                Stock stock = buildStock(result);
                stock.setType("2");
                stock.setMarketId(market.getId());
                stocks.add(stock);
            }
        } catch (Exception e) {
            log.error("请求匠山数据错误: config: {}", jsConfig, e);
        }
        threadLocal.remove();
        return stocks;
    }

    @Override
    public Stock getStock(Market market, String gid) {
        List<Stock> stocks = getStocks(market, Collections.singletonList(gid));
        if (LogicUtils.isNotEmpty(stocks)) {
            return stocks.get(0);
        }
        return null;
    }

    @Override
    public Stock getStockByWs(Market market, Stock stock) {
        return getStock(market, stock.getGid());
    }

    @Override
    public List<Stock> getStocks(Market market, List<String> gids) {
        JsConfig jsConfig = threadLocal.get();
        Map<String, Object> params = new HashMap<>();
        params.put("pid", LogicUtils.join(gids, ","));
        String s = HttpClient.sendPost(jsConfig.getDomain() + "/stock?key=" + jsConfig.getKey(), params);
        if (LogicUtils.isBlank(s)) {
            return null;
        }
        JSONArray arr = JSON.parseArray(s);
        if (LogicUtils.isEmpty(arr)) {
            return Collections.emptyList();
        }
        List<Stock> stocks = new ArrayList<>();
        for (int i = 0; i < arr.size(); i++) {
            JSONObject obj = arr.getJSONObject(i);
            Stock stock = buildStock(obj);
            stock.setMarketId(market.getId());
            stocks.add(stock);
        }
        threadLocal.remove();
        return stocks;
    }

    @Override
    public String getKline(Market market, String gid, String time) {
        JsConfig jsConfig = threadLocal.get();
        String s = HttpClient.sendGet(
                jsConfig.getDomain() + "/kline" + "?pid=" + gid + "&interval=" + time + "&key=" + jsConfig.getKey());
        if (Objects.equals("1", time)) {
            //单独处理下
            JSONArray objects = JSON.parseArray(s);
            if (LogicUtils.isNotEmpty(objects)) {
                for (int i = 0; i < objects.size(); i++) {
                    com.alibaba.fastjson.JSONObject jsonObject = objects.getJSONObject(i);
                    long value = jsonObject.getInteger("t").longValue();
                    long t = value * 1000;
                    DateTime dateTime = new DateTime(t);
                    jsonObject.put("time", dateTime.toString("yyyyMMdd"));
                }
                Map<String, List<Object>> list = objects.stream().collect(Collectors.groupingBy(s1 -> {
                    com.alibaba.fastjson.JSONObject obj = (com.alibaba.fastjson.JSONObject) s1;
                    return obj.getString("time");
                }));
                TreeMap<String, List<Object>> stringListTreeMap = new TreeMap<>(list);
                s = JSON.toJSONString(stringListTreeMap.lastEntry().getValue());
            }
        }
        threadLocal.remove();
        return s;
    }


    private static Stock buildStock(JSONObject result) {
        Stock stock = new Stock();
        stock.setName(result.getString("Name"));
        stock.setCode(result.getString("Symbol"));
        stock.setGid(result.getString("Id") != null?result.getString("Id"):result.getString("pid"));
        stock.setExchanges(result.getString("type") == null ? "all" : result.getString("type"));

        stock.setLast(StockApi.getBigDecimal(result, "Last", "last"));
        stock.setChg(StockApi.getBigDecimal(result, "Chg", "pc"));
        stock.setChgPct(StockApi.getBigDecimal(result, "ChgPct", "pcp"));
        stock.setHigh(StockApi.getBigDecimal(result, "High", "high"));
        stock.setLow(StockApi.getBigDecimal(result, "Low", "Low"));
        stock.setOpen(StockApi.getBigDecimal(result, "Open"));
        stock.setClose(StockApi.getBigDecimal(result, "PrevClose"));
        stock.setVolume(StockApi.getBigDecimal(result, "Volume"));
        stock.setAmounts(StockApi.getBigDecimal(result, "amounts"));
        return stock;
    }
}
