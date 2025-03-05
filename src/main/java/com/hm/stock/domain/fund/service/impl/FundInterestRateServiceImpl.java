package com.hm.stock.domain.fund.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.PageDTO;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hm.stock.domain.fund.entity.FundInterestRate;
import com.hm.stock.domain.fund.mapper.FundInterestRateMapper;
import com.hm.stock.domain.fund.service.FundInterestRateService;
import com.hm.stock.modules.common.PageDate;
import com.hm.stock.modules.common.PageParam;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FundInterestRateServiceImpl extends ServiceImpl<FundInterestRateMapper, FundInterestRate> implements FundInterestRateService {
    @Override
    public List<FundInterestRate> selectList(FundInterestRate query) {
        QueryWrapper<FundInterestRate> ew = new QueryWrapper<>();
        ew.setEntity(query);
        ew.orderByDesc("create_time");
        return list(ew);
    }

    @Override
    public PageDate<FundInterestRate> selectByPage(FundInterestRate query, PageParam page) {
        QueryWrapper<FundInterestRate> ew = new QueryWrapper<>();
        ew.setEntity(query);
        ew.orderByDesc("create_time");
        PageDTO<FundInterestRate> result = page(new PageDTO<>(page.getPageNo(), page.getPageSize()), ew);
        return PageDate.of(result);
    }

    @Override
    public FundInterestRate detail(Long id) {
        return getById(id);
    }

    @Override
    public Long add(FundInterestRate body) {
        boolean flag = save(body);
        return body.getId();
    }

    @Override
    public boolean delete(Long id) {
        return removeById(id);
    }

    @Override
    public boolean update(FundInterestRate body) {
        return updateById(body);
    }
}
