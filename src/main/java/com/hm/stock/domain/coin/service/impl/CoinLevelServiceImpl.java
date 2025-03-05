package com.hm.stock.domain.coin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.PageDTO;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hm.stock.domain.coin.entity.CoinLevel;
import com.hm.stock.domain.coin.mapper.CoinLevelMapper;
import com.hm.stock.domain.coin.service.CoinLevelService;
import com.hm.stock.modules.common.PageDate;
import com.hm.stock.modules.common.PageParam;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CoinLevelServiceImpl extends ServiceImpl<CoinLevelMapper, CoinLevel> implements CoinLevelService {
    @Override
    public List<CoinLevel> selectList(CoinLevel query) {
        QueryWrapper<CoinLevel> ew = new QueryWrapper<>();
        ew.setEntity(query);
        ew.orderByDesc("create_time");
        return list(ew);
    }

    @Override
    public PageDate<CoinLevel> selectByPage(CoinLevel query, PageParam page) {
        QueryWrapper<CoinLevel> ew = new QueryWrapper<>();
        ew.setEntity(query);
        ew.orderByDesc("create_time");
        PageDTO<CoinLevel> result = page(new PageDTO<>(page.getPageNo(), page.getPageSize()), ew);
        return PageDate.of(result);
    }

    @Override
    public CoinLevel detail(Long id) {
        return getById(id);
    }

    @Override
    public Long add(CoinLevel body) {
        boolean flag = save(body);
        return body.getId();
    }

    @Override
    public boolean delete(Long id) {
        return removeById(id);
    }

    @Override
    public boolean update(CoinLevel body) {
        return updateById(body);
    }
}
