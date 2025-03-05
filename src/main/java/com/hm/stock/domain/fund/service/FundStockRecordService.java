package com.hm.stock.domain.fund.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hm.stock.domain.fund.entity.FundStockRecord;
import com.hm.stock.modules.common.PageDate;
import com.hm.stock.modules.common.PageParam;

import java.util.List;

public interface FundStockRecordService extends IService<FundStockRecord> {
    List<FundStockRecord> selectList(FundStockRecord query);

    PageDate<FundStockRecord> selectByPage(FundStockRecord query, PageParam page);

    FundStockRecord detail(Long id);

    Long add(FundStockRecord body);

    boolean delete(Long id);

    boolean update(FundStockRecord body);
}
