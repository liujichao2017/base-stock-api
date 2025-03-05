package com.hm.stock.domain.fund.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hm.stock.domain.fund.entity.FundLists;
import com.hm.stock.domain.fund.entity.MemberFundRecord;
import com.hm.stock.domain.fund.vo.CountMemberFundVo;
import com.hm.stock.domain.fund.vo.MemberFundRecordVo;
import com.hm.stock.modules.common.PageDate;
import com.hm.stock.modules.common.PageParam;

import java.util.List;

public interface MemberFundRecordService extends IService<MemberFundRecord> {
    List<MemberFundRecord> selectList(MemberFundRecord query);

    PageDate<MemberFundRecordVo> selectByPage(MemberFundRecordVo query, PageParam page);

    MemberFundRecord detail(Long id);

    Long add(MemberFundRecord body);

    boolean delete(Long id);

    boolean update(MemberFundRecord body);

    List<CountMemberFundVo> countFund();

    Boolean sell(Long id);

    Boolean sell(FundLists fundLists, MemberFundRecord memberFundRecord);

    Boolean compute(FundLists fundLists, MemberFundRecord memberFundRecord, Long cycle);
}
