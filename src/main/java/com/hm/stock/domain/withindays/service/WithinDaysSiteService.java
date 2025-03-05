package com.hm.stock.domain.withindays.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hm.stock.domain.loan.vo.LoanApplyVo;
import com.hm.stock.domain.withindays.entity.WithinDaysSite;
import com.hm.stock.modules.common.PageDate;
import com.hm.stock.modules.common.PageParam;

import java.util.List;

public interface WithinDaysSiteService extends IService<WithinDaysSite> {
    List<WithinDaysSite> selectList(WithinDaysSite query);

    PageDate<WithinDaysSite> selectByPage(WithinDaysSite query, PageParam page);

    WithinDaysSite detail(Long id);

    Long add(WithinDaysSite body);

    boolean delete(Long id);

    boolean update(WithinDaysSite body);

    Boolean apply(LoanApplyVo body);
}
