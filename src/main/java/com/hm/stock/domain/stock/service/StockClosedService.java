package com.hm.stock.domain.stock.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hm.stock.domain.market.entity.Market;
import com.hm.stock.domain.stock.entity.ClosingDateConfig;

public interface StockClosedService extends IService<ClosingDateConfig> {


    boolean isClosed(Market market);

    void syncHolidayCalendar();
}
