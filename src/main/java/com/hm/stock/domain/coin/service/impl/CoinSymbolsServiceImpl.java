package com.hm.stock.domain.coin.service.impl;

import cn.hutool.core.collection.ConcurrentHashSet;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.PageDTO;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hm.stock.domain.coin.HuobiTool;
import com.hm.stock.domain.coin.entity.CoinKline;
import com.hm.stock.domain.coin.entity.CoinSymbols;
import com.hm.stock.domain.coin.mapper.CoinSymbolsMapper;
import com.hm.stock.domain.coin.service.CoinKlineService;
import com.hm.stock.domain.coin.service.CoinSymbolsService;
import com.hm.stock.domain.coin.vo.CoinKlineAdVo;
import com.hm.stock.domain.coin.vo.CoinKlineVo;
import com.hm.stock.domain.coin.wsclient.CoinWebSocketClient;
import com.hm.stock.modules.common.PageDate;
import com.hm.stock.modules.common.PageParam;
import com.hm.stock.modules.utils.DateTimeUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class CoinSymbolsServiceImpl extends ServiceImpl<CoinSymbolsMapper, CoinSymbols> implements CoinSymbolsService {

    @Autowired
    private CoinKlineService coinKlineService;


    @Override
    public List<CoinSymbols> selectList(CoinSymbols query) {
        QueryWrapper<CoinSymbols> ew = new QueryWrapper<>();
        ew.setEntity(query);
        ew.orderByDesc("create_time");
        return list(ew);
    }

    @Override
    public PageDate<CoinSymbols> selectByPage(CoinSymbols query, PageParam page) {
        QueryWrapper<CoinSymbols> ew = new QueryWrapper<>();
        query.setHot(1L);
        ew.setEntity(query);
        ew.orderByDesc("create_time");
        PageDTO<CoinSymbols> pageDTO = new PageDTO<>(page.getPageNo(), page.getPageSize());
        pageDTO.setOptimizeCountSql(false);
        PageDTO<CoinSymbols> result = page(pageDTO, ew);
        return PageDate.of(result);
    }

    @Override
    public CoinSymbols detail(Long id) {
        return getById(id);
    }

    @Override
    public Long add(CoinSymbols body) {
        boolean flag = save(body);
        return body.getId();
    }

    @Override
    public boolean delete(Long id) {
        return removeById(id);
    }

    @Override
    public boolean update(CoinSymbols body) {
        return updateById(body);
    }

    @Override
//    @Scheduled(cron = "0 * * * * ?")
    public void syncSymbols() {
        String symbols = HuobiTool.symbols();
        JSONObject res = JSONObject.parseObject(symbols);
        if (!"ok".equals(res.getString("status"))) {
            log.error("请求全部交易数据失败: {}", symbols);
            return;
        }
        JSONArray jsonArray = res.getJSONArray("data");
        for (int i = 0; i < jsonArray.size(); i++) {
            JSONObject item = jsonArray.getJSONObject(i);
            CoinSymbols coinSymbols = new CoinSymbols();
            coinSymbols.setSymbol(item.getString("symbol"));
            coinSymbols.setState(item.getString("state"));
            coinSymbols.setType(item.getString("1"));
            coinSymbols.setBcdn(item.getString("bcdn"));
            coinSymbols.setQcdn(item.getString("qcdn"));
            coinSymbols.setSn(item.getString("sn"));

            try {
                baseMapper.insert(coinSymbols);
            } catch (Exception e) {
                UpdateWrapper<CoinSymbols> uw = new UpdateWrapper<>();
                uw.eq("symbol", coinSymbols.getSymbol());
                baseMapper.update(coinSymbols, uw);
            }
        }
    }

    private static final Set<String> SUBSCRIBE_SYMBOLS = new ConcurrentHashSet<>();

    @Override
    public void subscribeSymbols(CoinWebSocketClient coinWebSocketClient, boolean retry) {
        QueryWrapper<CoinSymbols> ew = new QueryWrapper<>();
        ew.eq("hot", 1);
        List<CoinSymbols> coinSymbols = baseMapper.selectList(ew);
        log.info("需要订阅: {}个交易对, 链接状态: {}", coinSymbols.size(), coinWebSocketClient.isOpen());
        if (retry) {
            SUBSCRIBE_SYMBOLS.clear();
        }
        if (coinWebSocketClient.isOpen()) {
            for (CoinSymbols coinSymbol : coinSymbols) {
                JSONObject jsonObject = new JSONObject();
                String sub = String.format("market.%s.ticker", coinSymbol.getSymbol());
                if (!SUBSCRIBE_SYMBOLS.add(sub)) {
                    break;
                }
                jsonObject.put("sub", sub);
                jsonObject.put("id", UUID.randomUUID().toString());
                String msg = jsonObject.toString();
                coinWebSocketClient.send(msg);
                log.info("需要订阅: {} 完成", sub);
                try {
                    TimeUnit.MILLISECONDS.sleep(20);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    @Override
    public CoinKlineAdVo kline(CoinKlineVo coinKlineVo) {
        String kline = coinKlineService.kline(coinKlineVo, false);
        List<CoinKline> list = JSONObject.parseArray(kline, CoinKline.class);

        CoinKlineAdVo coinKlineAdVo = new CoinKlineAdVo();
        boolean min = coinKlineVo.getPeriod().endsWith("min");
        int len = list.size();
        len = Math.min(100, len);
        for (int size = len - 1; size >= 0; size--) {
            CoinKline coinKline = list.get(size);
            String dataStr = null;
            if (min) {
                dataStr = DateTimeUtil.toFormatStr(coinKline.getTs() * 1000, "yyyy-MM-dd HH:mm");
            } else {
                dataStr = DateTimeUtil.toFormatStr(coinKline.getTs() * 1000, "yyyy-MM-dd");
            }
            coinKlineAdVo.getTime().add(dataStr);
            coinKlineAdVo.getInfos().add(Arrays.asList(coinKline.getOpen(), coinKline.getClose(), coinKline.getLow(),
                                                       coinKline.getHigh()));
        }
        return coinKlineAdVo;
    }


}
