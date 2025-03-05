package com.hm.stock.domain.loan.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hm.stock.domain.loan.entity.MemberLoanRecord;
import com.hm.stock.domain.loan.vo.CountLoanVo;
import com.hm.stock.modules.common.PageDate;
import com.hm.stock.modules.common.PageParam;

import java.util.List;

public interface MemberLoanRecordService extends IService<MemberLoanRecord> {
    List<MemberLoanRecord> selectList(MemberLoanRecord query);

    PageDate<MemberLoanRecord> selectByPage(MemberLoanRecord query, PageParam page);

    MemberLoanRecord detail(Long id);

    Long add(MemberLoanRecord body);

    boolean delete(Long id);

    boolean update(MemberLoanRecord body);

    Boolean repayment(Long id);

    Boolean exitsLoan(Long userId);

    CountLoanVo countLoan();
}
