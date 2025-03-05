package com.hm.stock.domain.coin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hm.stock.domain.coin.entity.CoinDelivery;
import com.hm.stock.modules.common.PageDate;
import com.hm.stock.modules.common.PageParam;

import java.util.List;

public interface CoinDeliveryService extends IService<CoinDelivery> {
    List<CoinDelivery> selectList(CoinDelivery query);

    PageDate<CoinDelivery> selectByPage(CoinDelivery query, PageParam page);

    CoinDelivery detail(Long id);

    Long add(CoinDelivery body);

    boolean delete(Long id);

    boolean update(CoinDelivery body);
}
