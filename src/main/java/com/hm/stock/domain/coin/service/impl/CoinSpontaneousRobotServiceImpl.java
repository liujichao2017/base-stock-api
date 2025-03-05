package com.hm.stock.domain.coin.service.impl;

import cn.hutool.extra.spring.SpringUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.PageDTO;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hm.stock.domain.coin.entity.*;
import com.hm.stock.domain.coin.enums.KlinePeriodEnum;
import com.hm.stock.domain.coin.mapper.*;
import com.hm.stock.domain.coin.service.CoinDelegationService;
import com.hm.stock.domain.coin.service.CoinSpontaneousRobotService;
import com.hm.stock.domain.coin.vo.GenSpontaneousRobotVo;
import com.hm.stock.domain.ws.event.CoinEventManage;
import com.hm.stock.modules.common.CoinDelayed;
import com.hm.stock.modules.common.LogicUtils;
import com.hm.stock.modules.common.PageDate;
import com.hm.stock.modules.common.PageParam;
import com.hm.stock.modules.execptions.CommonResultCode;
import com.hm.stock.modules.utils.DateTimeUtil;
import com.hm.stock.modules.utils.ExecuteUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.DelayQueue;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
@Slf4j
public class CoinSpontaneousRobotServiceImpl extends ServiceImpl<CoinSpontaneousRobotMapper, CoinSpontaneousRobot> implements CoinSpontaneousRobotService {
    @Autowired
    private CoinSymbolsMapper coinSymbolsMapper;
    @Autowired
    private CoinSpontaneousMapper spontaneousMapper;
    @Autowired
    private CoinKlineMapper coinKlineMapper;
    @Autowired
    private CoinTradeDetailsMapper coinTradeDetailsMapper;
    @Autowired
    private CoinDelegationService coinDelegationService;
    @Autowired
    private CoinSpontaneousMapper coinSpontaneousMapper;

    @Autowired
    private CoinEventManage coinEventManage;

    @Override
    public List<CoinSpontaneousRobot> selectList(CoinSpontaneousRobot query) {
        QueryWrapper<CoinSpontaneousRobot> ew = new QueryWrapper<>();
        ew.setEntity(query);
        ew.orderByDesc("create_time");
        return list(ew);
    }

    @Override
    public PageDate<CoinSpontaneousRobot> selectByPage(CoinSpontaneousRobot query, PageParam page) {
        QueryWrapper<CoinSpontaneousRobot> ew = new QueryWrapper<>();
        ew.setEntity(query);
        ew.orderByDesc("create_time");
        PageDTO<CoinSpontaneousRobot> result = page(new PageDTO<>(page.getPageNo(), page.getPageSize()), ew);
        return PageDate.of(result);
    }

    @Override
    public CoinSpontaneousRobot detail(Long id) {
        return getById(id);
    }

    @Override
    public Long add(CoinSpontaneousRobot body) {
        boolean flag = save(body);
        return body.getId();
    }

    @Override
    public boolean delete(Long id) {
        return removeById(id);
    }

    @Override
    public boolean update(CoinSpontaneousRobot body) {
        return updateById(body);
    }


    private static final DelayQueue<CoinDelayed> DELAY_QUEUE = new DelayQueue<>();

    @Override
    public void monitorByDb() {
        while (true) {
            try {
                List<String> symbol = DELAY_QUEUE.stream().map(CoinDelayed::getSymbol).collect(Collectors.toList());
                QueryWrapper<CoinSpontaneous> ew = new QueryWrapper<>();
                ew.notIn(LogicUtils.isNotEmpty(symbol), "symbol", symbol);
                ew.eq("status", "1");
                List<CoinSpontaneous> coinSpontaneous = coinSpontaneousMapper.selectList(ew);
                if (LogicUtils.isNotEmpty(coinSpontaneous)) {
                    for (CoinSpontaneous spontaneous : coinSpontaneous) {
                        ExecuteUtil.COIN_GEN.execute(() -> {
                            amplitude(spontaneous.getSymbol(), System.currentTimeMillis());
                        });
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    TimeUnit.MINUTES.sleep(1);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    @PostConstruct
    public void init() {
        if (Boolean.FALSE.toString().equals(SpringUtil.getProperty("coin.enable"))){
            return;
        }
        log.info("初始化虚拟币机器人任务");
        new Thread(this::monitorByDb).start();
        new Thread(this::delayedTask).start();
    }

    @Override
    public void delayedTask() {
        while (true) {
            try {
                CoinDelayed take = null;
                while ((take = DELAY_QUEUE.take()) != null) {
                    String symbol = take.getSymbol();
                    long time = take.getTime();
                    ExecuteUtil.COIN_GEN.execute(() -> {
                        amplitude(symbol, time);
                    });
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void amplitude(String symbol, Long time) {
        CoinSymbols db = coinSymbolsMapper.selectBySymbol(symbol);
        if (LogicUtils.isNull(db)) {
            return;
        }
        if (LogicUtils.isNotEquals(db.getType(), "2")) {
            return;
        }
        CoinSpontaneous coinSpontaneous = spontaneousMapper.selectBySymbol(symbol);
        if (LogicUtils.isNull(coinSpontaneous)) {
            return;
        }
        if (LogicUtils.isNotEquals(coinSpontaneous.getStatus(), "1")) {
            return;
        }

        long ts = frequency(coinSpontaneous, time, db);
        DELAY_QUEUE.add(new CoinDelayed(symbol, ts));
        ExecuteUtil.COIN_WX.execute(() -> {
            coinEventManage.publish(db);
        });
        ExecuteUtil.COIN_TRADE.execute(() -> {
            coinDelegationService.trigger(symbol, db.getPrice());
        });
    }

    private boolean flag = true;

    @Override
    public Boolean genSpontaneous(GenSpontaneousRobotVo coinKlineVo) {
        LogicUtils.assertTrue(flag, CommonResultCode.INTERNAL_ERROR);
        CoinSymbols db = coinSymbolsMapper.selectBySymbol(coinKlineVo.getSymbol());
        LogicUtils.assertNotNull(db, CommonResultCode.INTERNAL_ERROR);
        LogicUtils.assertEquals(db.getType(), "2", CommonResultCode.INTERNAL_ERROR);
        CoinSpontaneous coinSpontaneous = spontaneousMapper.selectBySymbol(coinKlineVo.getSymbol());
        LogicUtils.assertNotNull(coinSpontaneous, CommonResultCode.INTERNAL_ERROR);
        Long start = DateTimeUtil.toLong(coinKlineVo.getStartTime());
        Long end = DateTimeUtil.toLong();

        QueryWrapper<CoinKline> ewKline = new QueryWrapper<>();
        ewKline.eq("symbol", coinKlineVo.getSymbol());
        ewKline.ge("ts", start / 1000);
        coinKlineMapper.delete(ewKline);

        QueryWrapper<CoinTradeDetails> ewTrade = new QueryWrapper<>();
        ewTrade.eq("symbol", coinKlineVo.getSymbol());
        ewTrade.ge("ts", start);

        coinTradeDetailsMapper.delete(ewTrade);

        CoinKline klineByDb = coinKlineMapper.selectLastKline(db.getSymbol(), start / 1000,
                                                              KlinePeriodEnum.MIN_1.getName());
        CoinSymbols coinSymbols = db;
        if (LogicUtils.isNotNull(klineByDb)) {
            coinSymbols.setSymbol(db.getSymbol());
            coinSymbols.setPrice(klineByDb.getClose());
            coinSymbols.setOpen(klineByDb.getOpen());
            coinSymbols.setClose(klineByDb.getClose());
            coinSymbols.setAmount(klineByDb.getAmount());
            coinSymbols.setCounts(BigDecimal.ZERO);
            coinSymbols.setLow(klineByDb.getLow());
            coinSymbols.setHigh(klineByDb.getHigh());
            coinSymbols.setVol(BigDecimal.ZERO);
        }

        ExecuteUtil.COIN_GEN.execute(() -> {
            try {
                flag = false;
                Long ts = start;
                while (ts < end) {
                    ts = frequency(coinSpontaneous, ts, coinSymbols);

                }
            } finally {
                flag = true;
            }
        });

        return true;
    }

    /**
     * @param coin
     * @param ts          毫秒
     * @param coinSymbols
     * @return
     */
    private Long frequency(CoinSpontaneous coin, Long ts, CoinSymbols coinSymbols) {
        CoinSpontaneousRobot robot = baseMapper.scan(coin.getId(), coinSymbols.getPrice());
        if (LogicUtils.isNull(robot)) {
            robot = getDefaultRobot();
        }
        BigDecimal amplitude = getAmplitude(robot.getMinAmplitude(), robot.getMaxAmplitude());
        BigDecimal num = getAmplitude(robot.getMinNum(), robot.getMaxNum());
        BigDecimal price = amplitude.add(coinSymbols.getPrice());
        if (price.compareTo(BigDecimal.ZERO) <= 0) {
            price = getAmplitude(new BigDecimal("0.001"), robot.getMaxAmplitude());
        }
        price = price.setScale(Math.max(robot.getMinAmplitude().scale(), robot.getMaxAmplitude().scale()), 4);


//        CoinKline kline = new CoinKline();
//        kline.setSymbol(coinSymbols.getSymbol());
//        kline.setTs(ts);
//
//        kline.setOpen(coinSymbols.getClose());
//        kline.setClose(price);
//        kline.setLow(getLow(price));
//        kline.setHigh(getHigh(price));
//        kline.setAmount(num);
//        kline.setVol(num.max(price));
//        kline.setCount((long) LogicUtils.getRandom(1, 5));

        // 更新最新价格
        boolean updateOpen = isUpdateOpen(ts, coinSymbols.getOpenTime());
        CoinSymbols update = new CoinSymbols();
        update.setSymbol(coin.getSymbol());
        update.setPrice(price);
        update.setOpenTime(updateOpen ? new Date() : null);
        update.setOpen(updateOpen ? price : null);
        update.setClose(price);
        update.setAmount(coinSymbols.getAmount().add(num));
        update.setCounts(coinSymbols.getCounts().add(BigDecimal.ONE));
        update.setLow(updateOpen ? price : coinSymbols.getLow().compareTo(price) > 0 ? price : coinSymbols.getLow());
        update.setHigh(updateOpen ? price : coinSymbols.getHigh().compareTo(price) < 0 ? price : coinSymbols.getHigh());
        update.setVol(coinSymbols.getVol().add(num.multiply(price)));
        QueryWrapper<CoinSymbols> uw = new QueryWrapper<>();
        uw.eq("symbol", coin.getSymbol());
        LogicUtils.assertEquals(coinSymbolsMapper.update(update, uw), 1, CommonResultCode.INTERNAL_ERROR);

        ExecuteUtil.COIN_GEN.execute(
                () -> {
                    // 更新K线数据
                    updateKline(update, ts, num);

                    // 更新交易记录
                    updateTradeDetail(update, ts, num);
                }
        );


        // 下次执行的时间
        long frequency = LogicUtils.getRandom(robot.getMinFrequency().intValue(), robot.getMaxFrequency().intValue());
        return ts + frequency;
    }

    private boolean isUpdateOpen(long ts, Date openTime) {
        if (LogicUtils.isNull(openTime)) {
            return true;
        }
        long openLong = openTime.getTime();
        openLong = openLong - openLong % KlinePeriodEnum.DAY_1.toMillis();
        ts = ts - ts % KlinePeriodEnum.DAY_1.toMillis();
        if (openLong < ts) {
            return true;
        }
        return false;
    }

    private static BigDecimal getHigh(BigDecimal price) {
        return price.add(getAmplitude(BigDecimal.ZERO, price.multiply(new BigDecimal("0.05"))));
    }

    private static BigDecimal getLow(BigDecimal price) {
        BigDecimal low = price.subtract(getAmplitude(BigDecimal.ZERO, price.multiply(new BigDecimal("0.05"))));
        if (low.compareTo(BigDecimal.ZERO) <= 0) {
            low = price;
        }
        return low;
    }

    private static BigDecimal getAmplitude(BigDecimal min, BigDecimal max) {
        if (min.compareTo(max) > 0) {
            return min;
        }
        min = min.stripTrailingZeros();
        max = max.stripTrailingZeros();
        BigDecimal amplitude = LogicUtils.getRandom(min, max);
        amplitude = amplitude.setScale(Math.max(min.scale(), max.scale()), 4);
        return amplitude;
    }

    public static void main(String[] args) {

        BigDecimal price = new BigDecimal("0.5");
        System.out.println(getLow(price));
        System.out.println(getHigh(price));

    }

    private void updateTradeDetail(CoinSymbols update, Long ts, BigDecimal num) {
        CoinTradeDetails ew = new CoinTradeDetails();
        ew.setSymbol(update.getSymbol());
        ew.setTs((ts));
        ew.setTradeId(System.currentTimeMillis());
        ew.setAmount(num);
        ew.setPrice(update.getPrice());
        ew.setDirection(LogicUtils.getRandom(0, 11) % 2 == 0 ? "sell" : "buy");
        coinTradeDetailsMapper.insert(ew);
    }

    private void updateKline(CoinSymbols update, Long ts, BigDecimal num) {
        long current = ts / 1000;
        String symbol = null;
        BigDecimal close = null;
        CoinKline klineByDb = coinKlineMapper.selectLastKline(update.getSymbol(), current,
                                                              KlinePeriodEnum.MIN_1.getName());
        if (LogicUtils.isNotNull(klineByDb)) {
            if (current < klineByDb.getTs() + KlinePeriodEnum.MIN_1.toSeconds()) {
                return;
            }
            ts = klineByDb.getTs();
            symbol = klineByDb.getSymbol();
            close = klineByDb.getClose();
        } else {
            ts = current - current % KlinePeriodEnum.MIN_1.toSeconds();
            symbol = update.getSymbol();
            close = update.getClose();
        }

        BigDecimal price = update.getPrice();
        for (KlinePeriodEnum periodEnum : KlinePeriodEnum.values()) {
            long time = ts + periodEnum.toSeconds();
            if (time % periodEnum.toSeconds() != 0) {
                break;
            }
            CoinKline insert = new CoinKline();
            insert.setSymbol(symbol);
            insert.setPeriod(periodEnum.getName());
            insert.setTs(time);
            insert.setCount((long) LogicUtils.getRandom(1, 100));
            insert.setAmount(num);
            insert.setOpen(close);
            insert.setClose(price);
            insert.setLow(update.getLow());
            insert.setHigh(update.getHigh());
            insert.setVol(num.max(price));
            coinKlineMapper.insert(insert);
        }

    }

    public CoinKline getCoinKline(CoinSymbols update, long current, String period) {
        CoinKline kline = coinKlineMapper.selectLastKline(update.getSymbol(), current, period);

        if (LogicUtils.isNotNull(kline)) {
            return kline;
        }
        kline = new CoinKline();
        kline.setSymbol(update.getSymbol());
        kline.setPeriod(period);
        kline.setCount(update.getCounts().longValue());
        kline.setAmount(update.getAmount());
        kline.setOpen(update.getOpen());
        kline.setClose(update.getClose());
        kline.setLow(update.getLow());
        kline.setHigh(update.getHigh());
        kline.setVol(update.getVol());
        return kline;
    }

    private CoinSpontaneousRobot getDefaultRobot() {
        CoinSpontaneousRobot coinSpontaneousRobot = new CoinSpontaneousRobot();
        coinSpontaneousRobot.setMinPrice(new BigDecimal("-0.005"));
        coinSpontaneousRobot.setMaxPrice(new BigDecimal("0.005"));
        coinSpontaneousRobot.setMinFrequency(250L);
        coinSpontaneousRobot.setMaxFrequency(750L);
        coinSpontaneousRobot.setMinAmplitude(new BigDecimal("0"));
        coinSpontaneousRobot.setMaxAmplitude(new BigDecimal("0"));
        coinSpontaneousRobot.setMinNum(new BigDecimal("1"));
        coinSpontaneousRobot.setMaxNum(new BigDecimal("5"));
        return coinSpontaneousRobot;
    }


}
