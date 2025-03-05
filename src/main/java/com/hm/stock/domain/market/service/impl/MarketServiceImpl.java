package com.hm.stock.domain.market.service.impl;

import java.util.Date;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hm.stock.domain.market.entity.Market;
import com.hm.stock.domain.market.mapper.MarketMapper;
import com.hm.stock.domain.market.service.MarketService;
import com.hm.stock.domain.site.entity.DataConfig;
import com.hm.stock.domain.site.service.DataConfigService;
import com.hm.stock.modules.i18n.I18nUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MarketServiceImpl extends ServiceImpl<MarketMapper, Market> implements MarketService {
    @Autowired
    private DataConfigService dataConfigService;


    @Override
    public List<Market> selectList(Market query) {
        QueryWrapper<Market> ew = new QueryWrapper<>();
        ew.setEntity(query);
        ew.eq("status", "1");
        ew.orderByAsc("sort");
        List<Market> list = list(ew);
        for (Market market : list) {
            market.setDataSourceJson(null);
            DataConfig dataConfig = new DataConfig();
            dataConfig.setGroup("market_name_lang");
            dataConfig.setType("str");
            dataConfig.setKey("market_name_" + I18nUtil.getLang() + "_" + market.getCountry());
            dataConfig.setVal(market.getCurrency());
            dataConfig.setRemark("市场语言转换");
            market.setName(dataConfigService.getByStr(dataConfig));
        }
        return list;
    }


}
