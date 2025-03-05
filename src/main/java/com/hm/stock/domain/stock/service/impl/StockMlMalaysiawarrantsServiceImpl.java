package com.hm.stock.domain.stock.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.PageDTO;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hm.stock.domain.stock.entity.StockMlMalaysiawarrants;
import com.hm.stock.domain.stock.mapper.StockMlMalaysiawarrantsMapper;
import com.hm.stock.domain.stock.service.StockMlMalaysiawarrantsService;
import com.hm.stock.modules.common.PageDate;
import com.hm.stock.modules.common.PageParam;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StockMlMalaysiawarrantsServiceImpl extends ServiceImpl<StockMlMalaysiawarrantsMapper, StockMlMalaysiawarrants> implements StockMlMalaysiawarrantsService {
    @Override
    public List<StockMlMalaysiawarrants> selectList(StockMlMalaysiawarrants query) {
        QueryWrapper<StockMlMalaysiawarrants> ew = new QueryWrapper<>();
        ew.setEntity(query);
        ew.orderByDesc("create_time");
        return list(ew);
    }

    @Override
    public PageDate<StockMlMalaysiawarrants> selectByPage(StockMlMalaysiawarrants query, PageParam page) {
        QueryWrapper<StockMlMalaysiawarrants> ew = new QueryWrapper<>();
        ew.setEntity(query);
        ew.orderByDesc("trade_volume");
        PageDTO<StockMlMalaysiawarrants> result = page(new PageDTO<>(page.getPageNo(), page.getPageSize()), ew);
        return PageDate.of(result);
    }

    @Override
    public StockMlMalaysiawarrants detail(Long id) {
        return getById(id);
    }

    @Override
    public Long add(StockMlMalaysiawarrants body) {
        boolean flag = save(body);
        return 1L;
    }

    @Override
    public boolean delete(Long id) {
        return removeById(id);
    }

    @Override
    public boolean update(StockMlMalaysiawarrants body) {
        return updateById(body);
    }
}
