package com.hm.stock.domain.fund.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.PageDTO;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hm.stock.domain.fund.entity.FundStockRecord;
import com.hm.stock.domain.fund.mapper.FundStockRecordMapper;
import com.hm.stock.domain.fund.service.FundStockRecordService;
import com.hm.stock.domain.stock.entity.StockMlMalaysiawarrants;
import com.hm.stock.domain.stock.mapper.StockMlMalaysiawarrantsMapper;
import com.hm.stock.modules.common.LogicUtils;
import com.hm.stock.modules.common.PageDate;
import com.hm.stock.modules.common.PageParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class FundStockRecordServiceImpl extends ServiceImpl<FundStockRecordMapper, FundStockRecord> implements FundStockRecordService {
    @Autowired
    private StockMlMalaysiawarrantsMapper stockMlMalaysiawarrantsMapper;

    @Override
    public List<FundStockRecord> selectList(FundStockRecord query) {
        QueryWrapper<FundStockRecord> ew = new QueryWrapper<>();
        ew.setEntity(query);
        ew.orderByDesc("create_time");
        return list(ew);
    }

    @Override
    public PageDate<FundStockRecord> selectByPage(FundStockRecord query, PageParam page) {
        QueryWrapper<FundStockRecord> ew = new QueryWrapper<>();
        ew.eq("b.status", "1");
        ew.eq(LogicUtils.isNotNull(query.getFundId()),"b.fund_id", query.getFundId());
        ew.orderByDesc("b.create_time");
        PageDTO<FundStockRecord> result = baseMapper.selectByPage(new PageDTO<>(page.getPageNo(), page.getPageSize()), ew);
        if (LogicUtils.isNotEmpty(result.getRecords())) {
            setStockInfo(result.getRecords());
        }
        return PageDate.of(result);
    }

    private void setStockInfo(List<FundStockRecord> records) {
        Map<String, FundStockRecord> fundStockRecordMap = records.stream().filter(f -> "ml".equals(f.getType()))
                .collect(Collectors.toMap(FundStockRecord::getCode, f -> f));
        if (LogicUtils.isEmpty(fundStockRecordMap)) {
            return;
        }
        QueryWrapper<StockMlMalaysiawarrants> ewM = new QueryWrapper<>();
        ewM.in("dw_symbol", fundStockRecordMap.keySet());
        List<StockMlMalaysiawarrants> stockMlMalaysiawarrants = stockMlMalaysiawarrantsMapper.selectList(ewM);
        for (StockMlMalaysiawarrants stockMlMalaysiawarrant : stockMlMalaysiawarrants) {
            FundStockRecord f = fundStockRecordMap.get(stockMlMalaysiawarrant.getDwSymbol());
            if (LogicUtils.isNull(f)) {
                continue;
            }
            f.setStockCode(stockMlMalaysiawarrant.getDwSymbol());
            f.setStockName(stockMlMalaysiawarrant.getUnderlyingName());
        }
    }

    @Override
    public FundStockRecord detail(Long id) {
        return getById(id);
    }

    @Override
    public Long add(FundStockRecord body) {
        boolean flag = save(body);
        return body.getId();
    }

    @Override
    public boolean delete(Long id) {
        return removeById(id);
    }

    @Override
    public boolean update(FundStockRecord body) {
        return updateById(body);
    }
}
