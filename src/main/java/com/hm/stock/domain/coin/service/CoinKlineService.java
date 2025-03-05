package com.hm.stock.domain.coin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hm.stock.domain.coin.entity.CoinKline;
import com.hm.stock.domain.coin.vo.CoinKlineVo;
import com.hm.stock.modules.common.PageDate;
import com.hm.stock.modules.common.PageParam;

import java.util.List;

public interface CoinKlineService extends IService<CoinKline> {
    String kline(CoinKlineVo query,boolean cache);

    PageDate<CoinKline> selectByPage(CoinKline query, PageParam page);

    CoinKline detail(Long id);

    Long add(CoinKline body);

    boolean delete(Long id);

    boolean update(CoinKline body);
}
