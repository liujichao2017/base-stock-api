package com.hm.stock.domain.market.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hm.stock.domain.market.entity.Market;

import java.util.List;

public interface MarketService extends IService<Market> {
    List<Market> selectList(Market query);
}
