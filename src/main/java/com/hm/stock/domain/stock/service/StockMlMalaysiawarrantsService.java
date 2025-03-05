package com.hm.stock.domain.stock.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hm.stock.domain.stock.entity.StockMlMalaysiawarrants;
import com.hm.stock.modules.common.PageDate;
import com.hm.stock.modules.common.PageParam;

import java.util.List;

public interface StockMlMalaysiawarrantsService extends IService<StockMlMalaysiawarrants> {
    List<StockMlMalaysiawarrants> selectList(StockMlMalaysiawarrants query);

    PageDate<StockMlMalaysiawarrants> selectByPage(StockMlMalaysiawarrants query, PageParam page);

    StockMlMalaysiawarrants detail(Long id);

    Long add(StockMlMalaysiawarrants body);

    boolean delete(Long id);

    boolean update(StockMlMalaysiawarrants body);
}
