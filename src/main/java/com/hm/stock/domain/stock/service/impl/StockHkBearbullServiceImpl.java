package com.hm.stock.domain.stock.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.PageDTO;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hm.stock.domain.stock.entity.StockHkBearbull;
import com.hm.stock.domain.stock.mapper.StockHkBearbullMapper;
import com.hm.stock.domain.stock.service.StockHkBearbullService;
import com.hm.stock.modules.common.LogicUtils;
import com.hm.stock.modules.common.PageDate;
import com.hm.stock.modules.common.PageParam;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StockHkBearbullServiceImpl extends ServiceImpl<StockHkBearbullMapper, StockHkBearbull> implements StockHkBearbullService {
    @Override
    public List<StockHkBearbull> selectList(StockHkBearbull query) {
        QueryWrapper<StockHkBearbull> ew = new QueryWrapper<>();
        ew.setEntity(query);
        ew.orderByDesc("create_time");
        return list(ew);
    }

    @Override
    public PageDate<StockHkBearbull> selectByPage(StockHkBearbull query, PageParam page) {
        QueryWrapper<StockHkBearbull> ew = new QueryWrapper<>();
//        ew.setEntity(query);
        ew.and(LogicUtils.isNotBlank(query.getCode()),
               (q) -> q.like("sym", query.getCode()).or().like("desp", query.getCode()));
        ew.orderByDesc("turn");
        PageDTO<StockHkBearbull> result = page(new PageDTO<>(page.getPageNo(), page.getPageSize()), ew);
        return PageDate.of(result);
    }

    @Override
    public StockHkBearbull detail(Long id) {
        return getById(id);
    }

    @Override
    public Long add(StockHkBearbull body) {
        boolean flag = save(body);
        return 0L;
    }

    @Override
    public boolean delete(Long id) {
        return removeById(id);
    }

    @Override
    public boolean update(StockHkBearbull body) {
        return updateById(body);
    }
}
