package com.hm.stock.domain.stock.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.PageDTO;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hm.stock.domain.auth.entity.SessionInfo;
import com.hm.stock.domain.stock.entity.Stock;
import com.hm.stock.domain.stock.entity.StockOption;
import com.hm.stock.domain.stock.mapper.StockOptionMapper;
import com.hm.stock.domain.stock.service.StockOptionService;
import com.hm.stock.domain.stock.service.StockService;
import com.hm.stock.modules.common.LogicUtils;
import com.hm.stock.modules.common.PageDate;
import com.hm.stock.modules.common.PageParam;
import com.hm.stock.modules.execptions.ErrorResultCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class StockOptionServiceImpl extends ServiceImpl<StockOptionMapper, StockOption> implements StockOptionService {
    @Autowired
    private StockService stockService;

    @Override
    public List<Stock> selectList(StockOption query) {
        QueryWrapper<StockOption> ew = new QueryWrapper<>();
        ew.setEntity(query);
        ew.orderByDesc("create_time");
        return convertStock(list(ew));
    }

    @Override
    public PageDate<Stock> selectByPage(StockOption query, PageParam page) {
        QueryWrapper<StockOption> ew = new QueryWrapper<>();
        ew.setEntity(query);
        ew.orderByDesc("create_time");
        PageDTO<StockOption> result = page(new PageDTO<>(page.getPageNo(), page.getPageSize()), ew);
        return PageDate.of(result.getTotal(),convertStock(result.getRecords()));
    }

    @Override
    public StockOption detail(Long id) {
        return getById(id);
    }

    @Override
    public Long add(StockOption body) {
        boolean flag = save(body);
        return body.getId();
    }

    @Override
    public boolean delete(Long id) {
        return removeById(id);
    }

    @Override
    public boolean update(StockOption body) {
        return updateById(body);
    }

    @Override
    public Boolean save(String gid) {
        Stock stock = stockService.getStock(gid);
        LogicUtils.assertNotNull(stock, ErrorResultCode.E000001);

        StockOption stockOption = new StockOption();
        stockOption.setMemberId(SessionInfo.getInstance().getId());
        stockOption.setStockGid(stock.getGid());
        stockOption.setType(stock.getType());
        if (LogicUtils.isNotEmpty(selectList(stockOption))) {
            return true;
        }
        save(stockOption);
        return true;
    }

    @Override
    public Boolean delete(String gid) {
        QueryWrapper<StockOption> ew = new QueryWrapper<>();
        ew.eq("stock_gid", gid);
        ew.eq("member_id", SessionInfo.getInstance().getId());
        return baseMapper.delete(ew) == 1;
    }

    @Override
    public Boolean isOption(String gid) {
        QueryWrapper<StockOption> ew = new QueryWrapper<>();
        ew.eq("stock_gid", gid);
        ew.eq("member_id", SessionInfo.getInstance().getId());
        return count(ew) != 0;
    }

    private List<Stock> convertStock(List<StockOption> list) {
        if (LogicUtils.isEmpty(list)) {
            return Collections.emptyList();
        }
        return stockService.getStockByGids(list.stream().map(StockOption::getStockGid).collect(Collectors.toSet()));
    }

}
