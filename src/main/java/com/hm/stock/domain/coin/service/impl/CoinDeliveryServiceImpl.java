package com.hm.stock.domain.coin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.PageDTO;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hm.stock.domain.coin.entity.CoinDelivery;
import com.hm.stock.domain.coin.mapper.CoinDeliveryMapper;
import com.hm.stock.domain.coin.service.CoinDeliveryService;
import com.hm.stock.modules.common.PageDate;
import com.hm.stock.modules.common.PageParam;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CoinDeliveryServiceImpl extends ServiceImpl<CoinDeliveryMapper, CoinDelivery> implements CoinDeliveryService {
    @Override
    public List<CoinDelivery> selectList(CoinDelivery query) {
        QueryWrapper<CoinDelivery> ew = new QueryWrapper<>();
        ew.setEntity(query);
        ew.orderByDesc("create_time");
        return list(ew);
    }

    @Override
    public PageDate<CoinDelivery> selectByPage(CoinDelivery query, PageParam page) {
        QueryWrapper<CoinDelivery> ew = new QueryWrapper<>();
        ew.setEntity(query);
        ew.orderByDesc("create_time");
        PageDTO<CoinDelivery> result = page(new PageDTO<>(page.getPageNo(), page.getPageSize()), ew);
        return PageDate.of(result);
    }

    @Override
    public CoinDelivery detail(Long id) {
        return getById(id);
    }

    @Override
    public Long add(CoinDelivery body) {
        boolean flag = save(body);
        return body.getId();
    }

    @Override
    public boolean delete(Long id) {
        return removeById(id);
    }

    @Override
    public boolean update(CoinDelivery body) {
        return updateById(body);
    }
}
