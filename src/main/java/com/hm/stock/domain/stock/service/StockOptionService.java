package com.hm.stock.domain.stock.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hm.stock.domain.stock.entity.Stock;
import com.hm.stock.domain.stock.entity.StockOption;
import com.hm.stock.modules.common.PageDate;
import com.hm.stock.modules.common.PageParam;

import java.util.List;

public interface StockOptionService extends IService<StockOption> {
    List<Stock> selectList(StockOption query);

    PageDate<Stock> selectByPage(StockOption query, PageParam page);

    StockOption detail(Long id);

    Long add(StockOption body);

    boolean delete(Long id);

    boolean update(StockOption body);

    Boolean save(String gid);

    Boolean delete(String gid);

    Boolean isOption(String gid);
}
