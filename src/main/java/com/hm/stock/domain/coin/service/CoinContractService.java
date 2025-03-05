package com.hm.stock.domain.coin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hm.stock.domain.coin.entity.CoinContract;
import com.hm.stock.modules.common.PageDate;
import com.hm.stock.modules.common.PageParam;

import java.util.List;

public interface CoinContractService extends IService<CoinContract> {
    List<CoinContract> selectList(CoinContract query);

    PageDate<CoinContract> selectByPage(CoinContract query, PageParam page);

    CoinContract detail(Long id);

    Long add(CoinContract body);

    boolean delete(Long id);

    boolean update(CoinContract body);
}
