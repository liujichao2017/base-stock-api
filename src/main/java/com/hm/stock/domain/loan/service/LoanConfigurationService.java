package com.hm.stock.domain.loan.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hm.stock.domain.loan.entity.LoanConfiguration;
import com.hm.stock.domain.loan.entity.MemberLoanRecord;
import com.hm.stock.domain.loan.vo.LoanApplyVo;
import com.hm.stock.domain.loan.vo.LoanConfigVo;
import com.hm.stock.modules.common.PageDate;
import com.hm.stock.modules.common.PageParam;

import java.math.BigDecimal;
import java.util.List;

public interface LoanConfigurationService extends IService<LoanConfiguration> {
    List<LoanConfiguration> selectList(LoanConfiguration query);

    PageDate<LoanConfiguration> selectByPage(LoanConfiguration query, PageParam page);

    LoanConfigVo detail(Long marketId);

    Long add(LoanConfiguration body);

    boolean delete(Long id);

    boolean update(LoanConfiguration body);

    Boolean apply(LoanApplyVo body);
}
