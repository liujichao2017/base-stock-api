package com.hm.stock.domain.fund.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hm.stock.domain.fund.entity.FundInterestRate;
import com.hm.stock.modules.common.PageDate;
import com.hm.stock.modules.common.PageParam;

import java.util.List;

public interface FundInterestRateService extends IService<FundInterestRate> {
    List<FundInterestRate> selectList(FundInterestRate query);

    PageDate<FundInterestRate> selectByPage(FundInterestRate query, PageParam page);

    FundInterestRate detail(Long id);

    Long add(FundInterestRate body);

    boolean delete(Long id);

    boolean update(FundInterestRate body);
}
