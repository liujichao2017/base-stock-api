package com.hm.stock.domain.coin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hm.stock.domain.coin.entity.CoinDelegation;
import com.hm.stock.modules.common.PageDate;
import com.hm.stock.modules.common.PageParam;

import java.math.BigDecimal;
import java.util.List;

public interface CoinDelegationService extends IService<CoinDelegation> {
    List<CoinDelegation> selectList(CoinDelegation query);

    PageDate<CoinDelegation> selectByPage(CoinDelegation query, PageParam page);

    CoinDelegation detail(Long id);

    Long add(CoinDelegation body);

    boolean delete(Long id);

    boolean update(CoinDelegation body);

    void trigger(String symbol, BigDecimal price);
}
