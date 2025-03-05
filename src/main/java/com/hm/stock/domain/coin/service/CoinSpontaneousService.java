package com.hm.stock.domain.coin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hm.stock.domain.coin.entity.CoinSpontaneous;
import com.hm.stock.modules.common.PageDate;
import com.hm.stock.modules.common.PageParam;

import java.util.List;

public interface CoinSpontaneousService extends IService<CoinSpontaneous> {
    List<CoinSpontaneous> selectList(CoinSpontaneous query);

    PageDate<CoinSpontaneous> selectByPage(CoinSpontaneous query, PageParam page);

    CoinSpontaneous detail(Long id);

    Long add(CoinSpontaneous body);

    boolean delete(Long id);

    boolean update(CoinSpontaneous body);
}
