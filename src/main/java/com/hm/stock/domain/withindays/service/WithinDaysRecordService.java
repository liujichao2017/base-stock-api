package com.hm.stock.domain.withindays.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hm.stock.domain.loan.vo.LoanApplyVo;
import com.hm.stock.domain.withindays.entity.WithinDaysRecord;
import com.hm.stock.modules.common.PageDate;
import com.hm.stock.modules.common.PageParam;

import java.util.List;

public interface WithinDaysRecordService extends IService<WithinDaysRecord> {
    List<WithinDaysRecord> selectList(WithinDaysRecord query);

    PageDate<WithinDaysRecord> selectByPage(WithinDaysRecord query, PageParam page);

    WithinDaysRecord detail(Long id);

    Long add(WithinDaysRecord body);

    boolean delete(Long id);

    boolean update(WithinDaysRecord body);
}
