package com.hm.stock.domain.coin.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.PageDTO;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hm.stock.domain.coin.HuobiTool;
import com.hm.stock.domain.coin.entity.CoinTradeDetails;
import com.hm.stock.domain.coin.mapper.CoinSymbolsMapper;
import com.hm.stock.domain.coin.mapper.CoinTradeDetailsMapper;
import com.hm.stock.domain.coin.service.CoinTradeDetailsService;
import com.hm.stock.domain.coin.vo.CoinTradeDetailsVo;
import com.hm.stock.modules.common.LogicUtils;
import com.hm.stock.modules.common.PageDate;
import com.hm.stock.modules.common.PageParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CoinTradeDetailsServiceImpl extends ServiceImpl<CoinTradeDetailsMapper, CoinTradeDetails> implements CoinTradeDetailsService {
    @Autowired
    private CoinSymbolsMapper coinSymbolsMapper;

    @Override
    public CoinTradeDetailsVo selectList(String symbol) {
        String type = coinSymbolsMapper.selectByType(symbol);
        CoinTradeDetailsVo coinTradeDetailsVo = new CoinTradeDetailsVo();
        if ("1".equals(type)) {
            String s = HuobiTool.historyTrade(symbol);
            JSONObject obj = JSONObject.parseObject(s);
            if ("ok".equals(obj.getString("status"))) {
                parseTradeData(symbol, obj, coinTradeDetailsVo);
            }
        } else {
            QueryWrapper<CoinTradeDetails> ew = new QueryWrapper<>();
            ew.eq("symbol", symbol);
            ew.orderByDesc("ts");
            ew.last("limit 100");
            List<CoinTradeDetails> list = list(ew);
            for (CoinTradeDetails coinTradeDetails : list) {
                String direction = coinTradeDetails.getDirection();
                if ("buy".equals(direction)) {
                    coinTradeDetailsVo.getBuy().add(coinTradeDetails);
                }
                if ("sell".equals(direction)) {
                    coinTradeDetailsVo.getSell().add(coinTradeDetails);
                }
            }
        }
        return coinTradeDetailsVo;
    }

    private static void parseTradeData(String symbol, JSONObject obj, CoinTradeDetailsVo coinTradeDetailsVo) {
        JSONArray data = obj.getJSONArray("data");
        for (int i = 0; i < data.size(); i++) {
            JSONObject item = data.getJSONObject(i);
            if (LogicUtils.isNotEmpty(item)) {
                JSONArray arr = item.getJSONArray("data");
                if (LogicUtils.isNotEmpty(arr)) {
                    for (int i1 = 0; i1 < arr.size(); i1++) {
                        JSONObject ar = arr.getJSONObject(i1);
                        CoinTradeDetails coinTradeDetails = new CoinTradeDetails();
                        coinTradeDetails.setSymbol(symbol);
                        coinTradeDetails.setTs(ar.getLong("ts"));
                        coinTradeDetails.setTradeId(ar.getLong("trade-id"));
                        coinTradeDetails.setAmount(ar.getBigDecimal("amount"));
                        coinTradeDetails.setPrice(ar.getBigDecimal("price"));
                        String direction = ar.getString("direction");
                        coinTradeDetails.setDirection(direction);
                        if ("buy".equals(direction)) {
                            coinTradeDetailsVo.getBuy().add(coinTradeDetails);
                        }
                        if ("sell".equals(direction)) {
                            coinTradeDetailsVo.getSell().add(coinTradeDetails);
                        }
                    }
                }
            }
        }
    }

    @Override
    public PageDate<CoinTradeDetails> selectByPage(CoinTradeDetails query, PageParam page) {
        QueryWrapper<CoinTradeDetails> ew = new QueryWrapper<>();
        ew.setEntity(query);
        ew.orderByDesc("create_time");
        PageDTO<CoinTradeDetails> result = page(new PageDTO<>(page.getPageNo(), page.getPageSize()), ew);
        return PageDate.of(result);
    }

    @Override
    public CoinTradeDetails detail(Long id) {
        return getById(id);
    }

    @Override
    public Long add(CoinTradeDetails body) {
        boolean flag = save(body);
        return body.getId();
    }

    @Override
    public boolean delete(Long id) {
        return removeById(id);
    }

    @Override
    public boolean update(CoinTradeDetails body) {
        return updateById(body);
    }
}
