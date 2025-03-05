package com.hm.stock.domain.stock.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hm.stock.domain.stock.entity.StockHkBearbull;
import com.hm.stock.modules.common.PageDate;
import com.hm.stock.modules.common.PageParam;

import java.util.List;

public interface StockHkBearbullService extends IService<StockHkBearbull> {
    List<StockHkBearbull> selectList(StockHkBearbull query);

    PageDate<StockHkBearbull> selectByPage(StockHkBearbull query, PageParam page);

    StockHkBearbull detail(Long id);

    Long add(StockHkBearbull body);

    boolean delete(Long id);

    boolean update(StockHkBearbull body);
}
