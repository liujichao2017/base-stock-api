package com.hm.stock.domain.dailylimit.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.PageDTO;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hm.stock.domain.dailylimit.entity.StockDailyLimit;
import com.hm.stock.domain.dailylimit.mapper.StockDailyLimitMapper;
import com.hm.stock.domain.dailylimit.service.StockDailyLimitService;
import com.hm.stock.domain.dailylimit.vo.DailyLimitVo;
import com.hm.stock.domain.stock.entity.Stock;
import com.hm.stock.domain.stock.mapper.MemberPositionMapper;
import com.hm.stock.domain.stock.service.MemberPositionService;
import com.hm.stock.domain.stock.service.StockService;
import com.hm.stock.domain.stock.service.impl.MemberPositionServiceImpl;
import com.hm.stock.domain.stock.service.impl.StockServiceImpl;
import com.hm.stock.modules.common.LogicUtils;
import com.hm.stock.modules.common.PageDate;
import com.hm.stock.modules.common.PageParam;
import com.hm.stock.modules.enums.YNEnum;
import com.hm.stock.modules.execptions.ErrorResultCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class StockDailyLimitServiceImpl extends ServiceImpl<StockDailyLimitMapper, StockDailyLimit> implements StockDailyLimitService {

    @Autowired
    private MemberPositionService memberPositionService;
    @Autowired
    private StockService stockService;


    @Override
    public List<StockDailyLimit> selectList(StockDailyLimit query) {
        QueryWrapper<StockDailyLimit> ew = new QueryWrapper<>();
        ew.setEntity(query);
        ew.orderByDesc("create_time");
        return list(ew);
    }

    @Override
    @Transactional
    public Boolean buy(DailyLimitVo dailyLimitVo) {
        StockDailyLimit stockDailyLimit = getById(dailyLimitVo.getId());
        LogicUtils.assertNotNull(stockDailyLimit, ErrorResultCode.E000028);
        LogicUtils.assertTrue(YNEnum.yes(stockDailyLimit.getStatus()), ErrorResultCode.E000038);
        LogicUtils.assertTrue(dailyLimitVo.getNum() > 0, ErrorResultCode.E000001);

        memberPositionService.buy(stockDailyLimit, dailyLimitVo);
        return true;
    }

    @Override
    public PageDate<StockDailyLimit> selectByPage(StockDailyLimit query, PageParam page) {
        QueryWrapper<StockDailyLimit> ew = new QueryWrapper<>();
        ew.setEntity(query);
        ew.orderByDesc("create_time");
        PageDTO<StockDailyLimit> result = page(new PageDTO<>(page.getPageNo(), page.getPageSize()), ew);
        return PageDate.of(result.getTotal(), convert(result.getRecords()));
    }

    private List<StockDailyLimit> convert(List<StockDailyLimit> records) {
        if (LogicUtils.isEmpty(records)) {
            return records;
        }
        Map<String, Stock> stockMap = records.stream().map(StockDailyLimit::getStockGid).map(stockService::getStockByWs).collect(Collectors.toMap(Stock::getGid, s -> s));
        for (StockDailyLimit record : records) {
            Stock stock = stockMap.get(record.getStockGid());
            record.setStockName(stock.getName());
            record.setStockCode(stock.getCode());
            record.setLast(stock.getLast());
            record.setChgPct(stock.getChgPct());
        }
        return records;
    }

    @Override
    public StockDailyLimit detail(Long id) {
        return getById(id);
    }

    @Override
    public Long add(StockDailyLimit body) {
        boolean flag = save(body);
        return body.getId();
    }

    @Override
    public boolean delete(Long id) {
        return removeById(id);
    }

    @Override
    public boolean update(StockDailyLimit body) {
        return updateById(body);
    }
}
