package com.hm.stock.domain.member.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hm.stock.domain.member.entity.Member;
import com.hm.stock.domain.member.entity.MemberFunds;
import com.hm.stock.domain.member.entity.MemberFundsLogs;
import com.hm.stock.domain.member.enums.FundsSourceEnum;
import com.hm.stock.domain.member.vo.FundConvertVo;
import com.hm.stock.domain.member.vo.MemberRealVo;
import com.hm.stock.domain.member.vo.MemberVo;
import com.hm.stock.domain.stock.entity.Stock;
import com.hm.stock.modules.common.PageDate;
import com.hm.stock.modules.common.PageParam;

import java.util.List;

public interface MemberService extends IService<Member> {


    MemberVo detail();

    boolean update(Member body);

    Boolean convertAmt(FundConvertVo fundConvertVo);

    Boolean realName(MemberRealVo memberRealVo);

    Member getAndCheck(FundsSourceEnum fundsSourceEnum);

    PageDate<MemberFundsLogs> fundsList(MemberFundsLogs query, PageParam page);

    void memberChange(Long memberId);
}
