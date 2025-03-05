package com.hm.stock.domain.stock.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hm.stock.domain.stock.entity.StockLevel;
import com.hm.stock.modules.common.PageDate;
import com.hm.stock.modules.common.PageParam;

import java.util.List;

public interface StockLevelService extends IService<StockLevel> {
    List<StockLevel> selectList(StockLevel query);

    PageDate<StockLevel> selectByPage(StockLevel query, PageParam page);

    StockLevel detail(Long id);

    Long add(StockLevel body);

    boolean delete(Long id);

    boolean update(StockLevel body);
}
