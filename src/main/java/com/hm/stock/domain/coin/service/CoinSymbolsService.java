package com.hm.stock.domain.coin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hm.stock.domain.coin.entity.CoinSymbols;
import com.hm.stock.domain.coin.vo.CoinKlineAdVo;
import com.hm.stock.domain.coin.vo.CoinKlineVo;
import com.hm.stock.domain.coin.vo.GenSpontaneousRobotVo;
import com.hm.stock.domain.coin.wsclient.CoinWebSocketClient;
import com.hm.stock.modules.common.PageDate;
import com.hm.stock.modules.common.PageParam;

import java.util.List;

public interface CoinSymbolsService extends IService<CoinSymbols> {
    List<CoinSymbols> selectList(CoinSymbols query);

    PageDate<CoinSymbols> selectByPage(CoinSymbols query, PageParam page);

    CoinSymbols detail(Long id);

    Long add(CoinSymbols body);

    boolean delete(Long id);

    boolean update(CoinSymbols body);

    void syncSymbols();

    void subscribeSymbols(CoinWebSocketClient coinWebSocketClient,boolean retry);

    CoinKlineAdVo kline(CoinKlineVo coinKlineVo);
}
