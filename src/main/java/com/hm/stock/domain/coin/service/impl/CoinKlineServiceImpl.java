package com.hm.stock.domain.coin.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.PageDTO;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hm.stock.domain.coin.HuobiTool;
import com.hm.stock.domain.coin.entity.CoinKline;
import com.hm.stock.domain.coin.mapper.CoinKlineMapper;
import com.hm.stock.domain.coin.mapper.CoinSymbolsMapper;
import com.hm.stock.domain.coin.service.CoinKlineService;
import com.hm.stock.domain.coin.vo.CoinKlineVo;
import com.hm.stock.modules.common.LogicUtils;
import com.hm.stock.modules.common.PageDate;
import com.hm.stock.modules.common.PageParam;
import com.hm.stock.modules.execptions.CommonResultCode;
import com.hm.stock.modules.redis.RedisClient;
import com.hm.stock.modules.redis.RedisKey;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
public class CoinKlineServiceImpl extends ServiceImpl<CoinKlineMapper, CoinKline> implements CoinKlineService {

    @Autowired
    private CoinSymbolsMapper coinSymbolsMapper;
    @Autowired
    private RedisClient redisClient;

    @Override
    public String kline(CoinKlineVo query,boolean cache) {
        String symbol = query.getSymbol();
        String period = query.getPeriod();
        LogicUtils.assertNotNull(symbol, CommonResultCode.PARAM_ERROR);
        LogicUtils.assertNotNull(period, CommonResultCode.PARAM_ERROR);
        String res = redisClient.get(RedisKey.klineKey(symbol, period));
        if (cache && LogicUtils.isNotBlank(res)) {
            return res;
        }
        String type = coinSymbolsMapper.selectByType(symbol);
        List<CoinKline> list = new ArrayList<>();
        if ("1".equals(type)) {
            String s = HuobiTool.historyKline(query.getSymbol(), period, 1000);
            JSONObject obj = JSONObject.parseObject(s);
            if ("ok".equals(obj.getString("status"))) {
                JSONArray data = obj.getJSONArray("data");
                for (int i = 0; i < data.size(); i++) {
                    JSONObject item = data.getJSONObject(i);
                    CoinKline kline = buildCoinKlin(symbol, period, item);
                    list.add(kline);
                }
            }
        } else {
            QueryWrapper<CoinKline> ew = new QueryWrapper<>();
            ew.eq("symbol", symbol);
            ew.eq("period", period);
            ew.orderByDesc("ts");
            ew.last("limit 1000");
            list = list(ew);
        }
        res = JSONObject.toJSONString(list);
        // 时间阶段: 1min, 5min, 15min, 30min, 60min, 1day,1mon, 1week, 1year
        if (period.endsWith("min")) {
            redisClient.set(RedisKey.klineKey(symbol, period), res, TimeUnit.MINUTES,
                            Integer.parseInt(period.replace("min", "")));
        } else if (period.endsWith("day")) {
            redisClient.set(RedisKey.klineKey(symbol, period), res, TimeUnit.HOURS, 1);
        } else {
            redisClient.set(RedisKey.klineKey(symbol, period), res, TimeUnit.DAYS, 20);
        }
        return res;
    }

    private static @NotNull CoinKline buildCoinKlin(String symbol, String period, JSONObject item) {
        CoinKline kline = new CoinKline();
        kline.setSymbol(symbol);
        kline.setPeriod(period);
        kline.setTs(item.getLong("id"));
        kline.setCount(item.getLong("count"));
        kline.setAmount(item.getBigDecimal("amount"));
        kline.setOpen(item.getBigDecimal("open"));
        kline.setClose(item.getBigDecimal("close"));
        kline.setLow(item.getBigDecimal("low"));
        kline.setHigh(item.getBigDecimal("high"));
        kline.setVol(item.getBigDecimal("vol"));
        return kline;
    }

    @Override
    public PageDate<CoinKline> selectByPage(CoinKline query, PageParam page) {
        QueryWrapper<CoinKline> ew = new QueryWrapper<>();
        ew.setEntity(query);
        ew.orderByDesc("create_time");
        PageDTO<CoinKline> result = page(new PageDTO<>(page.getPageNo(), page.getPageSize()), ew);
        return PageDate.of(result);
    }

    @Override
    public CoinKline detail(Long id) {
        return getById(id);
    }

    @Override
    public Long add(CoinKline body) {
        boolean flag = save(body);
        return body.getId();
    }

    @Override
    public boolean delete(Long id) {
        return removeById(id);
    }

    @Override
    public boolean update(CoinKline body) {
        return updateById(body);
    }
}
