package com.hm.stock.domain.coin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hm.stock.domain.coin.entity.CoinLevel;
import com.hm.stock.modules.common.PageDate;
import com.hm.stock.modules.common.PageParam;

import java.util.List;

public interface CoinLevelService extends IService<CoinLevel> {
    List<CoinLevel> selectList(CoinLevel query);

    PageDate<CoinLevel> selectByPage(CoinLevel query, PageParam page);

    CoinLevel detail(Long id);

    Long add(CoinLevel body);

    boolean delete(Long id);

    boolean update(CoinLevel body);
}
