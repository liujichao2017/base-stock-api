package com.hm.stock.domain.stock.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.PageDTO;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hm.stock.domain.market.entity.Market;
import com.hm.stock.domain.market.enums.StockDataSourceEnum;
import com.hm.stock.domain.market.mapper.MarketMapper;
import com.hm.stock.domain.stock.entity.Stock;
import com.hm.stock.domain.stock.entity.StockHis;
import com.hm.stock.domain.stock.mapper.StockHisMapper;
import com.hm.stock.domain.stock.mapper.StockMapper;
import com.hm.stock.domain.stock.service.StockService;
import com.hm.stock.domain.stock.vo.StockVo;
import com.hm.stock.domain.stockdata.StockApi;
import com.hm.stock.modules.common.LogicUtils;
import com.hm.stock.modules.common.PageDate;
import com.hm.stock.modules.common.PageParam;
import com.hm.stock.modules.execptions.ErrorResultCode;
import com.hm.stock.modules.redis.RedisClient;
import com.hm.stock.modules.redis.RedisKey;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Service
public class StockServiceImpl extends ServiceImpl<StockMapper, Stock> implements StockService {
    @Autowired
    private MarketMapper marketMapper;
    @Autowired
    private RedisClient redisClient;
    @Autowired
    private StockHisMapper stockHisMapper;


    @Override
    public PageDate<Stock> selectByPage(StockVo query, PageParam page) {
        QueryWrapper<Stock> ew = new QueryWrapper<>();
        ew.eq("is_show", "1");
        ew.eq(LogicUtils.isNotNull(query.getMarketId()), "market_id", query.getMarketId());
        ew.eq(LogicUtils.isNotBlank(query.getName()), "name", query.getName());
        ew.eq(LogicUtils.isNotBlank(query.getCode()), "code", query.getCode());
        ew.eq(LogicUtils.isNotBlank(query.getType()), "type", query.getType());
        ew.and(LogicUtils.isNotBlank(query.getKeywords()),
               e -> e.like("name", query.getKeywords()).or().eq("code", query.getKeywords()));
        ew.eq(LogicUtils.isNotBlank(query.getIsPopular()), "is_popular", query.getIsPopular());
        ew.orderByDesc("chg");
        PageDTO<Stock> pageDTO = new PageDTO<>(page.getPageNo(), page.getPageSize());
        pageDTO.setOptimizeCountSql(false);
        PageDTO<Stock> result = page(pageDTO, ew);

        List<Stock> records = converts(result.getRecords());
        if ("1".equals(query.getKline())) {
            for (Stock record : records) {
                record.setKline(kline(record.getGid(), "D"));
            }
        }
        return PageDate.of(result.getTotal(), records);
    }

    @Override
    public Stock detail(String gid) {
        return converts(Collections.singletonList(getStock(gid))).get(0);
    }

    @Override
    public String kline(String gid, String type) {
        String kline = redisClient.get(RedisKey.klineKey(gid, type));
//        String kline = null;
        if (kline == null) {
            Stock stock = getStock(gid);
            LogicUtils.assertNotNull(stock, ErrorResultCode.E000001);

            Market market = marketMapper.selectById(stock.getMarketId());
            StockApi api = StockApi.getInstance(market);
            kline = api.getKline(market, stock.getGid(), type);
            if (LogicUtils.isNotBlank(kline) && LogicUtils.isNotEquals(kline, "[]")) {
                int minutes = LogicUtils.isNumber(type) ? Integer.parseInt(type) : 30;
//                TimeUnit timeunit = LogicUtils.isNumber(type) ? TimeUnit.MINUTES : TimeUnit.HOURS;
                redisClient.set(RedisKey.klineKey(gid, type), kline, TimeUnit.MINUTES, minutes);
            }
        }
        return kline;
    }

    @Override
    public Stock getStock(String code) {
        QueryWrapper<Stock> ew = new QueryWrapper<>();
        ew.eq("gid", code);
        ew.last("limit 1");
        Stock stock = baseMapper.selectOne(ew);
        if (stock == null) {
            ew = new QueryWrapper<>();
            ew.eq("code", code);
            ew.last("limit 1");
            stock = baseMapper.selectOne(ew);
        }
        if (stock == null) {
            return null;
        }

        return converts(Collections.singletonList(stock)).get(0);
    }

    @Override
    public Stock getSingeStock(String code) {
        QueryWrapper<Stock> ew = new QueryWrapper<>();
        ew.eq("gid", code);
        ew.last("limit 1");
        Stock stock = baseMapper.selectOne(ew);
        if (stock == null) {
            ew = new QueryWrapper<>();
            ew.eq("code", code);
            ew.last("limit 1");
            stock = baseMapper.selectOne(ew);
        }
        if (stock == null) {
            return null;
        }

        return stock;
    }

    @Override
    public Stock getStockByLatest(Long id) {
        Stock stock = getById(id);

        Market market = marketMapper.selectById(stock.getMarketId());

        return getStockByLatest(stock.getGid(), market);
    }

    @Override
    public Stock getStockByLatest(String code) {
        Stock stock = getStock(code);
        LogicUtils.assertNotNull(stock, ErrorResultCode.E000015);
        Market market = marketMapper.selectById(stock.getMarketId());
        return getStockByLatest(stock, market);
    }

    @Override
    public Stock getStockByLatest(String code, Market market) {
        Stock stock = getStock(code);
        LogicUtils.assertNotNull(stock, ErrorResultCode.E000015);
        return getStockByLatest(stock, market);
    }

    @Override
    public List<Stock> getStockByGids(Collection<String> gids) {
        QueryWrapper<Stock> ew = new QueryWrapper<>();
        ew.in("gid", gids);
        return converts(baseMapper.selectList(ew));
    }

    @Override
    public List<Stock> getStockByIds(Collection<Long> ids) {
        return converts(baseMapper.selectBatchIds(ids));
    }

    @Override
    public List<Stock> getStockByCode(Collection<String> codes) {
        QueryWrapper<Stock> ew = new QueryWrapper<>();
        ew.in("code", codes);
        return converts(baseMapper.selectList(ew));
    }

    @Override
    public Stock getStockByWs(String gid) {
        Stock stock = getStock(gid);
        LogicUtils.assertNotNull(stock, ErrorResultCode.E000015);
        Market market = marketMapper.selectById(stock.getMarketId());
        Long id = stock.getId();
        StockApi instance = StockApi.getInstance(market);
        Stock stockByLatest = instance.getStockByWs(market, stock);
        stockByLatest.setId(id);
        stockByLatest.setName(stock.getName());
        stockByLatest.setCode(stock.getCode());
        stockByLatest.setGid(stock.getGid());
        updateById(stockByLatest);
        return stock;
    }

    private List<Stock> converts(List<Stock> records) {
        Map<Long, Market> map = new HashMap<>();
        for (Stock record : records) {
            Market market = map.computeIfAbsent(record.getMarketId(), marketMapper::selectById);
            if (!StockDataSourceEnum.getEnum(market.getDataSourceMark()).isStockHis()
                    || "2".equals(record.getType())) {
                Stock stock = getStockByLatest(record, market);
                record.setLast(stock.getLast());
                record.setChg(stock.getChg());
                record.setChgPct(stock.getChgPct());
                record.setOpen(stock.getOpen());
                record.setClose(stock.getClose());
                record.setHigh(stock.getHigh());
                record.setLow(stock.getLow());
                record.setAmounts(stock.getAmounts());
                record.setVolume(stock.getVolume());
                baseMapper.updateById(record);
            }
        }
//        List<String> gids = new ArrayList<>();
//        for (Stock record : records) {
//            Market market = map.computeIfAbsent(record.getMarketId(), marketMapper::selectById);
//            if (StockDataSourceEnum.getEnum(market.getDataSourceMark()).isStockHis()) {
//                gids.add(record.getGid());
//            }
//        }
//        List<StockHis> stockHisList = new ArrayList<>();
//        if (LogicUtils.isNotEmpty(gids)) {
//            QueryWrapper<StockHis> ew = new QueryWrapper<>();
//            ew.in("stock_full_code", gids);
//            stockHisList = stockHisMapper.selectList(ew);
//        }
//
//        Map<String, StockHis> stockHisMap = stockHisList.stream()
//                .collect(Collectors.toMap(StockHis::getStockFullCode, s -> s));
//        for (Stock record : records) {
//            Market market = map.computeIfAbsent(record.getMarketId(), marketMapper::selectById);
//            if (StockDataSourceEnum.getEnum(market.getDataSourceMark()).isStockHis() && "1".equals(record.getType())) {
//                StockHis stockHis = stockHisMap.get(record.getGid());
//                if (stockHis == null) {
//                    continue;
//                }
//                record.setLast(stockHis.getZuix());
//                record.setChg(stockHis.getZdf());
//                record.setChgPct(stockHis.getZzd());
//                record.setOpen(stockHis.getJk());
//                record.setClose(stockHis.getZuos());
//                record.setHigh(stockHis.getZg());
//                record.setLow(stockHis.getZd());
//                record.setAmounts(stockHis.getCje());
//                record.setVolume(stockHis.getCjl());
//            } else {
//                Stock stock = getStockByLatest(record, market);
//                record.setLast(stock.getLast());
//                record.setChg(stock.getChg());
//                record.setChgPct(stock.getChgPct());
//                record.setOpen(stock.getOpen());
//                record.setClose(stock.getClose());
//                record.setHigh(stock.getHigh());
//                record.setLow(stock.getLow());
//                record.setAmounts(stock.getAmounts());
//                record.setVolume(stock.getVolume());
//            }
//            baseMapper.updateById(record);
//        }
        return records;
    }

    private static StockHis createEmpty() {
        StockHis stockHis = new StockHis();
        stockHis.setZuix(new BigDecimal("0"));
        stockHis.setZuos(new BigDecimal("0"));
        stockHis.setZdf(new BigDecimal("0"));
        stockHis.setZzd(new BigDecimal("0"));
        stockHis.setJk(new BigDecimal("0"));
        stockHis.setZg(new BigDecimal("0"));
        stockHis.setZd(new BigDecimal("0"));
        stockHis.setZt(new BigDecimal("0"));
        stockHis.setCjl(new BigDecimal("0"));
        stockHis.setCje(new BigDecimal("0"));
        return stockHis;
    }

    public Stock getStockByLatest(Stock stock, Market market) {
        Long id = stock.getId();
        StockApi instance = StockApi.getInstance(market);
        Stock stockByLatest = instance.getStock(market, stock.getGid());
        stockByLatest.setId(id);
        stockByLatest.setName(stock.getName());
        stockByLatest.setCode(stock.getCode());
        stockByLatest.setGid(stock.getGid());
        updateById(stockByLatest);
        return stockByLatest;
    }
}
