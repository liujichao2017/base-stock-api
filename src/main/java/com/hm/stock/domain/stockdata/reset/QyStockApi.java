package com.hm.stock.domain.stockdata.reset;

import cn.hutool.extra.spring.SpringUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.hm.stock.domain.market.entity.Market;
import com.hm.stock.domain.stock.entity.Stock;
import com.hm.stock.domain.stock.mapper.StockMapper;
import com.hm.stock.domain.stockdata.StockApi;
import com.hm.stock.domain.stockdata.reset.vo.JsConfig;
import com.hm.stock.modules.common.LogicUtils;
import com.hm.stock.modules.utils.ExecuteUtil;
import com.hm.stock.modules.utils.HttpClient;
import com.hm.stock.modules.utils.JsonUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 匠山数据
 */
@Slf4j
@Component
public class QyStockApi implements StockApi {

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
                String res = HttpClient.sendGet(jsConfig.getDomain() + "/stock/stock-list" + String.format(
                        "?country=" + jsConfig.getCountryId() + "&pageSize=1000&page=%d&apikey=%s", i, jsConfig.getKey()));
                if (LogicUtils.isBlank(res)) {
                    break;
                }
                JSONObject obj = JSONObject.parseObject(res);
                JSONArray arr = obj.getJSONArray("list");
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
            log.error("请求启源数据错误: config: {}", jsConfig, e);
        }
        threadLocal.remove();
        return stocks;
    }

    @Override
    public List<Stock> getIndex(Market market) {
        JsConfig jsConfig = threadLocal.get();
        List<Stock> stocks = new ArrayList<>();
        try {
            String res = HttpClient.sendGet(jsConfig.getDomain() + "/stock/index-list" + String.format(
                    "?country=" + jsConfig.getCountryId() + "&apikey=%s", jsConfig.getKey()));
            if (LogicUtils.isBlank(res)) {
                return stocks;
            }
            JSONArray arr = JSONObject.parseObject(res).getJSONArray("list");
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
            log.error("请求启源数据错误: config: {}", jsConfig, e);
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
        List<Stock> stocks = getStock(market, gids);
        if (stocks.size() < gids.size()) {
            stocks = new ArrayList<>(stocks);
            List<Stock> index = getIndex(market);
            StockMapper mapper = SpringUtil.getBean(StockMapper.class);
            for (Stock stock : index) {
                ExecuteUtil.STOCK.execute(() -> {
                    QueryWrapper<Stock> ew = new QueryWrapper<>();
                    ew.eq("gid", stock.getGid());
                    mapper.update(stock, ew);
                });
                if (gids.contains(stock.getGid())) {
                    stocks.add(stock);
                }
            }
        }
        return stocks;
    }

    @Override
    public String getKline(Market market, String gid, String time) {
        String timeframe = "";
        switch (time) {
            case "D":
                timeframe = "1D";
                break;
            case "W":
                timeframe = "1W";
                break;
            case "M":
                timeframe = "1M";
                break;
            default:
                timeframe = Integer.parseInt(time) + "min";
                break;
        }
        JsConfig jsConfig = threadLocal.get();
        String s = HttpClient.sendGet(
                jsConfig.getDomain() + "/stock/history" + "?country=" + jsConfig.getCountryId() + "&code=" + gid + "&timeframe=" + timeframe + "&apikey=" + jsConfig.getKey());
        s = JSONObject.parseObject(s).getJSONArray("list").toJSONString();
        threadLocal.remove();
        return s;
    }


    private static Stock buildStock(JSONObject result) {
        Stock stock = new Stock();
        stock.setName(result.getString("name"));
        stock.setCode(result.getString("symbol"));
        stock.setGid(result.getString("code"));
        stock.setExchanges(result.getString("exchange"));

        stock.setLast(StockApi.getBigDecimal(result, "price"));
        stock.setChg(StockApi.getBigDecimal(result, "change"));
        stock.setChgPct(StockApi.getBigDecimal(result, "changePercent"));
        stock.setHigh(StockApi.getBigDecimal(result, "high"));
        stock.setLow(StockApi.getBigDecimal(result, "low"));
        stock.setOpen(StockApi.getBigDecimal(result, "open"));
        stock.setClose(StockApi.getBigDecimal(result, "prevClose"));
        stock.setVolume(StockApi.getBigDecimal(result, "volume"));
        stock.setAmounts(StockApi.getBigDecimal(result, "amounts"));
        return stock;
    }


    private List<Stock> getStock(Market market, List<String> gids) {
        JsConfig jsConfig = threadLocal.get();
        String s = HttpClient.sendPost(
                jsConfig.getDomain() + "/stock/post-codes?country=" + jsConfig.getCountryId() + "&apikey=" + jsConfig.getKey(),
                gids);
        if (LogicUtils.isBlank(s)) {
            return null;
        }
        JSONArray arr = JSON.parseObject(s).getJSONArray("list");
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
}
