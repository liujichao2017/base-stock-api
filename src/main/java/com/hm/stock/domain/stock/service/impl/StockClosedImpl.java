package com.hm.stock.domain.stock.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hm.stock.domain.market.entity.Market;
import com.hm.stock.domain.market.service.MarketService;
import com.hm.stock.domain.site.entity.DataConfig;
import com.hm.stock.domain.site.service.DataConfigService;
import com.hm.stock.domain.stock.entity.ClosingDateConfig;
import com.hm.stock.domain.stock.mapper.ClosingDateConfigMapper;
import com.hm.stock.domain.stock.service.StockClosedService;
import com.hm.stock.modules.common.LogicUtils;
import com.hm.stock.modules.enums.YNEnum;
import com.hm.stock.modules.utils.DateTimeUtil;
import com.hm.stock.modules.utils.HttpClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StockClosedImpl extends ServiceImpl<ClosingDateConfigMapper, ClosingDateConfig> implements StockClosedService {

    @Autowired
    private DataConfigService dataConfigService;
    @Autowired
    private ClosingDateConfigMapper closingDateConfigMapper;
    @Autowired
    private MarketService marketService;

    @Override
    public boolean isClosed(Market market) {
        DataConfig dataConfig = new DataConfig();
        dataConfig.setGroup("stock");
        dataConfig.setType("str");
        dataConfig.setKey("close");
        dataConfig.setVal("0");
        dataConfig.setRemark("股票交易周末/节假日开关: 1:开启校验,0:关闭交易");
        if (YNEnum.no(dataConfigService.getByStr(dataConfig))) {
            return false;
        }
        if (DateTimeUtil.isWeekend(market.getTimeZone())){
            return true;
        }
        QueryWrapper<ClosingDateConfig> ew = new QueryWrapper<>();
        ew.eq("closing_date", DateTimeUtil.getNowDateStr(market.getTimeZone()));
        ew.eq("market_id", market.getId());
        return baseMapper.selectCount(ew) >= 1L;
    }

    @Override
    public void syncHolidayCalendar() {
        // 查询所有（股票）市场，type=stock
        QueryWrapper<Market> ew = new QueryWrapper<>();
        ew.eq("type","stock");
        List<Market> holidayCalendar = marketService.list(ew);

//        List<DataConfig> holidayCalendar = dataConfigService.getGroup("HolidayCalendar");
        if (LogicUtils.isEmpty(holidayCalendar)) {
            return;
        }
        String url = "http://206.238.199.99:9305/api/reptile/holiday/calendar/list?country=%s";
        for (Market market : holidayCalendar) {
            String s = HttpClient.sendGet(String.format(url, market.getSyncName()));
            JSONObject res = JSONObject.parseObject(s);
            if (LogicUtils.isNotEquals(res.getInteger("code"), 200)) {
                continue;
            }
            JSONArray arr = res.getJSONArray("data");
            for (int i = 0; i < arr.size(); i++) {
                JSONObject item = arr.getJSONObject(i);
                ClosingDateConfig closingDateConfig = new ClosingDateConfig();
                closingDateConfig.setMarketId(market.getId());
                closingDateConfig.setClosingDate(item.getString("date"));
                try {
                    closingDateConfigMapper.insert(closingDateConfig);
                } catch (Exception e) {

                }
            }
        }
    }
}
