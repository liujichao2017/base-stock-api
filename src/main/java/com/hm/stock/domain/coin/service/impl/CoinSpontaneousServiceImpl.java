package com.hm.stock.domain.coin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.PageDTO;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hm.stock.domain.coin.entity.CoinSpontaneous;
import com.hm.stock.domain.coin.mapper.CoinSpontaneousMapper;
import com.hm.stock.domain.coin.service.CoinSpontaneousService;
import com.hm.stock.modules.common.PageDate;
import com.hm.stock.modules.common.PageParam;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CoinSpontaneousServiceImpl extends ServiceImpl<CoinSpontaneousMapper, CoinSpontaneous> implements CoinSpontaneousService {
    @Override
    public List<CoinSpontaneous> selectList(CoinSpontaneous query) {
        QueryWrapper<CoinSpontaneous> ew = new QueryWrapper<>();
        ew.setEntity(query);
        ew.orderByDesc("create_time");
        return list(ew);
    }

    @Override
    public PageDate<CoinSpontaneous> selectByPage(CoinSpontaneous query, PageParam page) {
        QueryWrapper<CoinSpontaneous> ew = new QueryWrapper<>();
        ew.setEntity(query);
        ew.orderByDesc("create_time");
        PageDTO<CoinSpontaneous> result = page(new PageDTO<>(page.getPageNo(), page.getPageSize()), ew);
        return PageDate.of(result);
    }

    @Override
    public CoinSpontaneous detail(Long id) {
        return getById(id);
    }

    @Override
    public Long add(CoinSpontaneous body) {
        boolean flag = save(body);
        return body.getId();
    }

    @Override
    public boolean delete(Long id) {
        return removeById(id);
    }

    @Override
    public boolean update(CoinSpontaneous body) {
        return updateById(body);
    }
}
