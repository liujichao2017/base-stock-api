package com.hm.stock.domain.dailylimit.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hm.stock.domain.dailylimit.entity.StockDailyLimit;
import com.hm.stock.domain.dailylimit.vo.DailyLimitVo;
import com.hm.stock.modules.common.PageDate;
import com.hm.stock.modules.common.PageParam;

import java.util.List;

public interface StockDailyLimitService extends IService<StockDailyLimit> {
    List<StockDailyLimit> selectList(StockDailyLimit query);

    PageDate<StockDailyLimit> selectByPage(StockDailyLimit query, PageParam page);

    StockDailyLimit detail(Long id);

    Long add(StockDailyLimit body);

    boolean delete(Long id);

    boolean update(StockDailyLimit body);

    Boolean buy(DailyLimitVo dailyLimitVo);
}
