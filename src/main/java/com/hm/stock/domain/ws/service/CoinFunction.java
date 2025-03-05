package com.hm.stock.domain.ws.service;

import com.alibaba.fastjson.JSONArray;
import com.hm.stock.domain.stock.entity.Stock;
import com.hm.stock.domain.stock.service.StockService;
import com.hm.stock.domain.ws.event.CoinEventManage;
import com.hm.stock.domain.ws.event.StockEventManage;
import com.hm.stock.modules.common.LogicUtils;
import com.hm.stock.modules.execptions.CommonResultCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CoinFunction {

    @Autowired
    private CoinEventManage coinEventManage;

    public void subscribe(String sessionId, String param) {
        List<String> gids = JSONArray.parseArray(param, String.class);
        LogicUtils.assertNotEmpty(gids, CommonResultCode.AUT_ERROR);
        coinEventManage.subscribe(sessionId, gids);
    }

    public void cancel(String sessionId,String param) {
        List<String> gids = param == null ? null : JSONArray.parseArray(param, String.class);
        coinEventManage.cancel(sessionId,gids);
    }



}
