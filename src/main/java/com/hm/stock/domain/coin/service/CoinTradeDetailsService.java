package com.hm.stock.domain.coin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hm.stock.domain.coin.entity.CoinTradeDetails;
import com.hm.stock.domain.coin.vo.CoinTradeDetailsVo;
import com.hm.stock.modules.common.PageDate;
import com.hm.stock.modules.common.PageParam;

import java.util.List;

public interface CoinTradeDetailsService extends IService<CoinTradeDetails> {
    CoinTradeDetailsVo selectList(String symbol);

    PageDate<CoinTradeDetails> selectByPage(CoinTradeDetails query, PageParam page);

    CoinTradeDetails detail(Long id);

    Long add(CoinTradeDetails body);

    boolean delete(Long id);

    boolean update(CoinTradeDetails body);
}
