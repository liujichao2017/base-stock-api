package com.hm.stock.domain.fund.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hm.stock.domain.fund.entity.FundLists;
import com.hm.stock.domain.fund.vo.FundListsVo;
import com.hm.stock.modules.common.PageDate;
import com.hm.stock.modules.common.PageParam;

import java.util.List;

public interface FundListsService extends IService<FundLists> {
    List<FundLists> selectList(FundLists query);

    PageDate<FundLists> selectByPage(FundLists query, PageParam page);

    FundLists detail(Long id);

    Long add(FundLists body);

    boolean delete(Long id);

    boolean update(FundLists body);

    Boolean buy(FundListsVo fundListsVo);

    Boolean compute(Integer days, String date);

}
