package com.hm.stock.domain.ws.service;

import com.alibaba.fastjson.JSONArray;
import com.hm.stock.domain.stock.entity.Stock;
import com.hm.stock.domain.stock.service.StockService;
import com.hm.stock.domain.ws.event.StockEventManage;
import com.hm.stock.modules.common.LogicUtils;
import com.hm.stock.modules.execptions.CommonResultCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
public class StockFunction {

    @Autowired
    private StockEventManage stockEventManage;

    @Autowired
    private StockService service;

    public void subscribe(String sessionId, String param) {
        List<String> gids = JSONArray.parseArray(param, String.class);
        LogicUtils.assertNotEmpty(gids, CommonResultCode.AUT_ERROR);
        stockEventManage.subscribe(sessionId, gids);
        List<Stock> stocks = service.getStockByGids(gids);
        stockEventManage.publish(stocks);
    }

    public void cancel(String sessionId,String param) {
        List<String> gids = param == null ? null : JSONArray.parseArray(param, String.class);
        stockEventManage.cancel(sessionId,gids);
    }



}
